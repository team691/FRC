
// To execute:
// ./basket_finder_bin <port> [-disp]
//     port number defaults to 20000 display defaults to off

// Code based on Auto-generated C Code - S2i Harpia
/*
 *	In order to compile this source code run, in a terminal window, the following command:
 *	g++ sourceCodeName.cc `pkg-config --libs --cflags opencv` -o outputProgramName
 *	
 *	the `pkg-config ... opencv` parameter is a inline command that returns the path to both 
 *	the libraries and the headers necessary when using opencv. The command also returns other necessary compiler options.
 */
// header:
#include <stdio.h>
#include <stdlib.h>
#include <cv.h>
#include <cvaux.h>
#include <highgui.h>
#include <math.h>

#include "super_sock.h"
#include "KPacket.h"
#include "frc2012.h"

using namespace std;
using namespace Team691;



#define PI 3.1415926535898
double rads(double degs)
{
	return (PI/180 * degs);
}

//Routines to findSquares
double angle( CvPoint* pt1, CvPoint* pt2, CvPoint* pt0 )
{
    double dx1 = pt1->x - pt0->x;
    double dy1 = pt1->y - pt0->y;
    double dx2 = pt2->x - pt0->x;
    double dy2 = pt2->y - pt0->y;
    return (dx1*dx2 + dy1*dy2)/sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
}

CvSeq* findSquares4( IplImage* img, CvMemStorage* storage, int minArea, int maxArea)
{
    CvSeq* contours;
    int i, c, l, N = 11;
    int thresh = 50;
    CvSize sz = cvSize( img->width & -2, img->height & -2 );
    IplImage* timg = cvCloneImage( img ); // make a copy of input image
    IplImage* gray = cvCreateImage( sz, 8, 1 ); 
    IplImage* pyr = cvCreateImage( cvSize(sz.width/2, sz.height/2), 8, 3 );
    IplImage* tgray;
    CvSeq* result;
    double s, t;

    if(minArea == -1)
        minArea = 0;
    if(maxArea == -1)
        maxArea = (img->width * img->height);

    CvSeq* squares = cvCreateSeq( 0, sizeof(CvSeq), sizeof(CvPoint), storage );
		

    cvSetImageROI( timg, cvRect( 0, 0, sz.width, sz.height ));
		
    // down-scale and upscale the image to filter out the noise
    cvPyrDown( timg, pyr, CV_GAUSSIAN_5x5 );
    cvPyrUp( pyr, timg, CV_GAUSSIAN_5x5 );
    tgray = cvCreateImage( sz, 8, 1 );
		
    // find squares in every color plane of the image
    for( c = 0; c < 3; c++ )
    {
        // extract the c-th color plane
        cvSetImageCOI( timg, c+1 );
        cvCopy( timg, tgray, 0 );
        for( l = 0; l < N; l++ )
        {
            if( l == 0 )
            {
                cvCanny( tgray, gray, 0, thresh, 5 );
                cvDilate( gray, gray, 0, 1 );
            }
            else
            {
                cvThreshold( tgray, gray, (l+1)*255/N, 255, CV_THRESH_BINARY );
            }
            cvFindContours( gray, storage, &contours, sizeof(CvContour),
                            CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0) );
            while( contours )
            {
                result = cvApproxPoly( contours, sizeof(CvContour), storage,
                                       CV_POLY_APPROX_DP, cvContourPerimeter(contours)*0.02, 0 );
                if( result->total == 4 &&
                    fabs(cvContourArea(result,CV_WHOLE_SEQ)) > minArea &&
                    fabs(cvContourArea(result,CV_WHOLE_SEQ)) < maxArea &&
                    cvCheckContourConvexity(result) )
                {
                    s = 0;
										
                    for( i = 0; i < 5; i++ )
                    {
                        if( i >= 2 )
                        {
                            t = fabs(angle(
                                           (CvPoint*)cvGetSeqElem( result, i ),
                                           (CvPoint*)cvGetSeqElem( result, i-2 ),
                                           (CvPoint*)cvGetSeqElem( result, i-1 )));
                            s = s > t ? s : t;
                        }
                    }
                    if( s < 0.3 )
                        for( i = 0; i < 4; i++ )
                            cvSeqPush( squares,
                                       (CvPoint*)cvGetSeqElem( result, i ));

                                       
                }
                contours = contours->h_next;
            }
        }
    }
    cvReleaseImage( &gray );
    cvReleaseImage( &pyr );
    cvReleaseImage( &tgray );
    cvReleaseImage( &timg );
    return squares;
}

double drawSquares( IplImage* cpy, CvSeq* squares )
{
    CvSeqReader reader;
    int i;
    cvStartReadSeq( squares, &reader, 0 );
    for( i = 0; i < squares->total; i += 4 )
    {
        CvPoint pt[4], *rect = pt;
        int count = 4;
        CV_READ_SEQ_ELEM( pt[0], reader );
        CV_READ_SEQ_ELEM( pt[1], reader );
        CV_READ_SEQ_ELEM( pt[2], reader );
        CV_READ_SEQ_ELEM( pt[3], reader );
        cvPolyLine( cpy, &rect, &count, 1, 1, CV_RGB(0,255,0), 1, CV_AA, 0 ); 


        cvCircle( cpy, pt[0], 3, CV_RGB(255,0,0), 1, CV_AA, 0 );
        cvCircle( cpy, pt[1], 3, CV_RGB(0,127,0), 1, CV_AA, 0 );
        cvCircle( cpy, pt[2], 3, CV_RGB(0,0,255), 1, CV_AA, 0 );
        cvCircle( cpy, pt[3], 3, CV_RGB(0,255,255), 1, CV_AA, 0 );

        CvPoint center;
        find_square_center(rect, &center);

        printf("Center = (%d,%d) ", center.x, center.y);

        printf("Square = ( %d,%d %d,%d %d,%d %d,%d )\n", 
               pt[0].x, pt[0].y,
               pt[1].x, pt[1].y,
               pt[2].x, pt[2].y,
               pt[3].x, pt[3].y);

        cvCircle( cpy, center, 3, CV_RGB(255,0,255), 1, CV_AA, 0 );

    }
	return (double)squares->total;
}

//End of routines to findSquares

			

int GetColor(IplImage * imagem, int x, int y)
{
	return   (int)(((uchar*)(imagem->imageData + imagem->widthStep*y))[x]);
}

void SetColor(IplImage * imagem, int x, int y, uchar color)
{
	((uchar*)(imagem->imageData + imagem->widthStep*y))[x] = color;
}


void CheckImg(IplImage * img, uchar c_value, uchar tolerance)
{
	uchar min,max;
	int y_It,x_It;
	if((int)c_value < (int)tolerance)
		tolerance = c_value;

	if(((int)c_value+(int)tolerance) > 255)
		tolerance = 255 - c_value;

	min = c_value - tolerance;
	max = c_value + tolerance;

	for(y_It=0;y_It<(img->height);y_It++)
		for(x_It=0;x_It<(img->width);x_It++)
		{
            uchar val;
            val = GetColor(img,x_It,y_It);
            if(val >= min && val <= max)
                SetColor(img,x_It,y_It,255);
            else
                SetColor(img,x_It,y_It,0);
		}
}

CvPoint GetCenter(IplImage * src, long int * nOfPts)//, long int * numOfPoints)
{
	long int numOfMatchingPoints;
	long int posXsum;
	long int posYsum;
	int x_It, y_It;
	CvPoint Center;
	
	posXsum = 0;
	posYsum = 0;
	numOfMatchingPoints = 0;

	for(y_It=0;y_It<(src->height);y_It++)
		for(x_It=0;x_It<(src->width);x_It++)
			if(GetColor(src,x_It,y_It))
			{
				posXsum += x_It;
				posYsum += y_It;
				numOfMatchingPoints++;
			}

	if(numOfMatchingPoints > 0)
	{
		Center.x = (int)(posXsum/numOfMatchingPoints);
		Center.y = (int)(posYsum/numOfMatchingPoints);
	}
	else
		numOfMatchingPoints = -1;
    // 	(*numOfPoints) = numOfMatchingPoints;
	if(nOfPts)
		*nOfPts = numOfMatchingPoints;

	return Center;
}


double dist22Points(CvPoint a, CvPoint b)
{
	int xD,yD;
	xD = a.x - b.x;
	yD = a.y - b.y;

	xD = (xD>0)?xD:-xD;
	yD = (yD>0)?yD:-yD;

	return (double)(xD*xD + yD*yD);
}

double GetVariance(IplImage * src,CvPoint center)//, long int * numOfPoints)
{
	long int numOfMatchingPoints;
	double distSquaresSum;
	double variance;
	int x_It,y_It;

	numOfMatchingPoints = 0;
	distSquaresSum = 0.0;


	for(y_It=0;y_It<(src->height);y_It++)
		for(x_It=0;x_It<(src->width);x_It++)
			if(GetColor(src,x_It,y_It))
			{
				numOfMatchingPoints++;
				distSquaresSum += dist22Points(center,cvPoint(x_It,y_It));
			}

	if(numOfMatchingPoints)
		variance = distSquaresSum/numOfMatchingPoints;
	else
		variance = -1;


	return variance;
}

long int CheckForColor(IplImage * src, IplImage * dst, uchar * c_value, uchar * tolerance, CvPoint * pointCenter, double * variance)
{
	uchar B,B_T,G,G_T,R,R_T;
	int i;
	long int numOfPoints;
	CvPoint centro;
	IplImage * m_pChans[3] = {NULL,NULL,NULL};

	numOfPoints = 0;

	B = c_value[0];
	G = c_value[1];
	R = c_value[2];

	B_T = tolerance[0];
	G_T = tolerance[1];
	R_T = tolerance[2];

	for(i=0;i<3;i++)
		m_pChans[i] = cvCreateImage(cvGetSize(src),IPL_DEPTH_8U, 1);

	cvSplit(src,m_pChans[0],m_pChans[1],m_pChans[2], NULL);

	CheckImg(m_pChans[0],B,B_T);
	CheckImg(m_pChans[1],G,G_T);
	CheckImg(m_pChans[2],R,R_T);

	cvAnd(m_pChans[0], m_pChans[1], dst, NULL );
	cvAnd(m_pChans[2], dst, dst, NULL );

	centro = GetCenter(dst,&numOfPoints);//,&numOfPoints);

	if(numOfPoints != -1)
		*variance = GetVariance(dst,centro);

	pointCenter->x = centro.x;
	pointCenter->y = centro.y;

	cvReleaseImage( &m_pChans[0] );
	cvReleaseImage( &m_pChans[1] );
	cvReleaseImage( &m_pChans[2] );

	return numOfPoints;
}
			
int main(int argc, char ** argv)
{
	//declaration block
    IplImage * block1_img_o1 = NULL;
    IplImage * block2_img_i1 = NULL;
    IplImage * block2_img_o1 = NULL;
    IplImage * block5_img_i1 = NULL;
    IplImage * block5_img_o2 = NULL;
    double block5_double_o1;
    CvMemStorage * block5_storage = NULL;
    IplImage * block4_img_i1 = NULL;
    IplImage * block4_img_o1 = NULL;
    int end;  end = 0; int key; 
    CvCapture * block1_capture = NULL; 
    IplImage * block1_frame = NULL; 
    block1_capture = cvCaptureFromCAM(0); 


    //FIRST -- New variables
    CvMemStorage * reduced_storage = NULL;
    char * port = (char *)"20000";
    char * inet_addr = (char *)"0.0.0.0";
    bool display = false;

    cout << "argc = " << argc << endl;

    SuperSock s;
    if(argc >= 2)
        port = (char *)argv[1];

    if(argc >= 3)
        display = true;


    /*
     * Init socket
     */

    if(s.init(port, inet_addr))
    {
        cout << "Socket initialized" << endl;
    }
    else
    {
        return -1;
    }
    
    

    int i = 0;

    while(!end) 
    {	 

        printf("\nFrame %d\n", i++);

        cvGrabFrame (block1_capture); 
        block1_frame = cvRetrieveFrame (block1_capture); 

        //execution block
        //Weight: 1
        // Live Mode 
        block1_img_o1 = cvCloneImage( block1_frame );
        block2_img_i1 = cvCloneImage(block1_img_o1);// IMAGE conection
        block5_img_i1 = cvCloneImage(block1_img_o1);// IMAGE conection
        //Weight: 2

//         if(block2_img_i1){
//             block2_img_o1 = cvCloneImage(block2_img_i1);
//             cvNamedWindow("block2_img_o1",CV_WINDOW_AUTOSIZE );
//             cvShowImage("block2_img_o1",block2_img_i1);} 
        //Weight: 2

        if(block5_img_i1){
            block5_img_o2 = cvCloneImage(block5_img_i1);
            block5_storage = cvCreateMemStorage(0);

            // FIRST -- Get the squares list and reduce redundant squares
            CvSeq * all_squares = findSquares4( block5_img_o2, block5_storage , 500, 100000);
            reduced_storage = cvCreateMemStorage(0);

            SquareHolder sq;
            CvSeq * reduced_squares = reduceSquares(reduced_storage, all_squares, 5, sq);

            // FIRST -- Output data to a socket
            sockWrite(s, sq);


            block5_double_o1 = (double)drawSquares( block5_img_o2, reduced_squares );
          


            //  block5_double_o1 = (double)drawSquares( block5_img_o2, findSquares4( block5_img_o2, block5_storage , 500, 100000) );
            cvClearMemStorage( block5_storage );
        }
        block4_img_i1 = cvCloneImage(block5_img_o2);// IMAGE conection
        //Weight: 3

        if(display)
        {
            if(block4_img_i1){
                block4_img_o1 = cvCloneImage(block4_img_i1);
                cvNamedWindow("block4_img_o1",CV_WINDOW_AUTOSIZE );
                cvShowImage("block4_img_o1",block4_img_i1);} 
        }

        key = cvWaitKey (16);
        if(key != -1)
            end = 1;
        //deallocation block
        cvReleaseImage(&block1_img_o1);
        cvReleaseImage(&block2_img_o1);
        cvReleaseImage(&block2_img_i1);
        cvReleaseImage(&block5_img_o2);
        cvReleaseImage(&block5_img_i1);
        cvReleaseMemStorage(&block5_storage );
        cvReleaseMemStorage(&reduced_storage );
        cvReleaseImage(&block4_img_o1);
        cvReleaseImage(&block4_img_i1);
    }
    cvReleaseCapture(&block1_capture);
    return 0;
} //closing main()
