/** 
 * @file  frc2012.h
 *
 * $Id: $
 *
 * @author  Team 691
 * @date    February 18, 2012
 *
 *
 */ 
#ifndef  FRC2012_H
#define  FRC2012_H

#include <stdio.h>
#include <stdlib.h>
#include <cv.h>
#include <cvaux.h>
#include <highgui.h>
#include <math.h>

namespace Team691
{

    using namespace std;


    // Constants

    // Dist in feet= (1/height in pixels) * h_factor
//    const float h_factor = 861.0; // foot*pixels
    const float h_factor = 615.0; // foot*pixels

    // Target sizes - inches
    const float target_h = 18.0;     // height
    const float target_w = 24.0;     // width
    const float top_bottom_h = 70.0; // top to bottom center dist
    const float left_right_w = 54.75; // left to right center dist

    // Some useful ratios
    const float target_h_to_w = target_h/target_w;
    const float target_h_to_tb = target_h/top_bottom_h; 
    const float target_h_to_lr = target_h/left_right_w;
    const float tb_to_lr = top_bottom_h/left_right_w;

    // Where are the squares?
    // The bottom center position is never below this.
    const float min_bottom_center = 250.0;  //pixels
    const float max_bottom_center = 400.0;
    // The left and right center is never greater than this.
    const float max_lr_center = 270.0;      //pixels
    const float min_y_lr_with_top = 100.0;  // Min LR y pixel when top is visible 
    const float min_top_distance = 144.0;   // Below this dist we don't even look for the top target.

    // Center lines
    const float frame_center_x = 290.0; //310.0; 
    //offset has been corrected mechanicly
    const float frame_center_y = 240.0;

    // Table of dist to RPM
    struct d_rpm_element
       {
	  float dist;
	  float rpm;
       };

   d_rpm_element d_rpm_table[4][10] = {
                                     { //TOP = [0][X]
                                       {72.0,  1600.0}, //1500
                                       {84.0,  1700.0}, //1600
                                       {140.0, 1900.0}, //1800
                                       {200.0, 2300.0}, //2200
                                       {250.0, 2600.0}, //2600
                                       {-1.0, -1.0}    // End of data
                                     },
                                     { //LEFT = [1][X]
                                       {110.0,  1600.0},
                                       {140.0,  1780.0},
                                       {190.0,  1975.0},
                                       {250.0,  2380.0},
                                       {-1.0, -1.0}    // End of data
                                     },
                                     { //BOTTOM = [2][X]
                                       {110.0,  1510.0},
                                       {140.0,  1695.0},
                                       {190.0,  1880.0},
                                       {240.0,  2150.0},
                                       {-1.0, -1.0}    // End of data
                                     },
                                     { //RIGHT = [3][X]
                                       {110.0,  1600.0}, //COPY OF LEFT!!!
                                       {140.0,  1780.0},
                                       {190.0,  1975.0},
                                       {250.0,  2380.0},
                                       {-1.0, -1.0}    // End of data
                                     }
                                   };
   /*
   //old table, commented so we don't have to retype
   d_rpm_element d_rpm_table[] = { {72.0,  1500.0},
                                   {84.0,  1600.0},
                                   {140.0, 1800.0},
                                   {200.0, 2200.0},
                                   {-1.0, -1.0}    // End of data
                                 };
   /**/
				    

    // Identify targets
    enum
    {
        TOP       = 1,
        LEFT      = 2,
        BOTTOM    = 4,
        RIGHT     = 8,
        TOP_IDX       = 0,
        LEFT_IDX      = 1,
        BOTTOM_IDX    = 2,
        RIGHT_IDX     = 3,
    };

    float pix_to_dist(float pixels)
    {
        return (1/pixels)*h_factor;
    }

// FIRST -- Find the center of a square.  This is a Simple routine 
// that does not care what order the corner points are in.
//target
// @param[in] - points - Pointer to a list of 4 points that represent a square
// @param[out] - center - A single point representing the center of the square
//		target
void find_square_center(const CvPoint* points, CvPoint* center)
{
    center->x = 0;
    center->y = 0;
    for (int i=0; i < 4; i++)
    {
        center->x += points[i].x;
        center->y += points[i].y;
        
    }
    center->x /= 4;
    center->y /= 4;
    return;

}

// FIRST -- Find the size of a square.  This is a Simple routine 
// that does not care what order the corner points are in.
//
// @param[in] - points - Pointer to targeta list of 4 points that represent a square
// @param[out] - size_x - The size of the square's x-axis (average)
// @param[out] - size_y - The size of the square's y-axis (average)
//		
void find_square_size(const CvPoint* points, float* size_x, float* size_y)
{
    // First find the center
    CvPoint2D32f center;
    center.x = 0.0;
    center.y = 0.0;
    for (int i=0; i < 4; i++)
    {
        center.x += points[i].x;
        center.y += points[i].y;
        
    }
    center.x /= 4.0;
    center.y /= 4.0;

    // Now find the size

    *size_x = 0.0;
    *size_y = 0.0;

    for (int i=0; i < 4; i++)
    {
        *size_x += fabs(points[i].x - center.x);
        *size_y += fabs(points[i].y - center.y);
        
    }

    *size_x /= 2.0;
    *size_y /= 2.0;

    return;

}

float h_to_dist(float h)
{
    return 12.0 * (h_factor/h);
}

float h_to_speed(float h, int target)
{
    int t = target;
    float d = h_to_dist(h);
    float rpm = 0.0;

    for(int i = 0 ; d_rpm_table[t][i].dist >= 0.0; i++)
        {
	     rpm = d_rpm_table[t][i].rpm;  // Set to the current value in case there
                                        // is no higher value.
             if(d < d_rpm_table[t][i].dist)
                {
                // Special case if i=0 just leave it at the lowest setting
		if(i > 0)
                  {
                   rpm = d_rpm_table[t][i-1].rpm + 
                         (d_rpm_table[t][i].rpm - d_rpm_table[t][i-1].rpm) *
                         (d - d_rpm_table[t][i-1].dist) / (d_rpm_table[t][i].dist- d_rpm_table[t][i-1].dist);
                   
                   return rpm;
                   }
		else
	 	   return rpm;
                }
       }
	
    return rpm;
}



// FIRST -- A structure to hold the data we need about the square
class Square
{
public:
    Square(): 
        center_x(0.0),
        center_y(0.0),
        size_x(0.0),
        size_y(0.0),
        square_id(0),
        dist(0.0),
        instances(0),
        link(NULL)
    {
        corner[0].x = 0;
        corner[1].x = 0;
        corner[2].x = 0;
        corner[3].x = 0;
        corner[0].y = 0;
        corner[1].y = 0;
        corner[2].y = 0;
        corner[3].y = 0;
    }

    Square(const CvPoint* pt)
    {
        Square();
        for (int n = 0; n < 4; n++)
        {
            corner[n].x = pt[n].x;
            corner[n].y = pt[n].y;
        }
        CvPoint center;
        find_square_center(pt, &center);
        center_x = (float)center.x;
        center_y = (float)center.y;
        
        find_square_size(pt, 
                         &size_x, 
                         &size_y);
        instances = 0;
        link = NULL;
        
    }

    ~Square()
    {
        if(link != NULL) 
            free(link);
    }

    Square& operator= (const Square& param)
    {
        corner[0].x = param.corner[0].x;
        corner[1].x = param.corner[1].x;
        corner[2].x = param.corner[2].x;
        corner[3].x = param.corner[3].x;
        corner[0].y = param.corner[0].y;
        corner[1].y = param.corner[1].y;
        corner[2].y = param.corner[2].y;
        corner[3].y = param.corner[3].y;

        center_x = param.center_x;
        center_y = param.center_y;
        size_x = param.size_x;
        size_y = param.size_y;
        square_id = param.square_id;
        dist = param.dist;
        instances = param.instances;
        link = NULL;
        return *this;
    }

    CvPoint corner[4];
    float  center_x;
    float  center_y;
    float    size_x;
    float    size_y;
    int   square_id;     // top = 1, left = 2, bottom = 3, right = 4
    float      dist;     // Dist to target
    int   instances;     // How many squares at this center?
    Square* link;
};

class SquareHolder
{
public:
    SquareHolder(int _dist = 5):
        sq_list(NULL),
        dist(_dist) { }

    ~SquareHolder()
    {
        if(sq_list != NULL) 
            free(sq_list);
    }

    void add_pt(const CvPoint* pt)
    {
        Square* s = new Square(pt);
        // If there are no points, add this one
        if (sq_list == NULL)
        {
            sq_list = s;
            s->instances++;
        }
        else
        {
            //Traverse the list to see if we already have this point
            for(Square* next = sq_list; next != NULL; next = next->link)
            {
                if ((fabs(next->center_x - s->center_x) <= (float)dist) &&
                    (fabs(next->center_y - s->center_y) <= (float)dist))
                {

                    // Add the data from this square to the size and center
                    // location of the square.
                    next->center_x = (next->center_x*next->instances + s->center_x)/
                                               (next->instances +1);
                    next->center_y = (next->center_y*next->instances + s->center_y)/
                                               (next->instances +1);

                    next->size_x = (next->size_x*next->instances + s->size_x)/
                                               (next->instances +1);
                    next->size_y = (next->size_y*next->instances + s->size_y)/
                                               (next->instances +1);
                    next->instances++;

                    free(s);
                    return;
                }
                if(next->link == NULL)
                {            
                    next->link = s;
                    s->instances++;
                    return;
                }
            }
        }
        return;
    }

    void fill_seq(CvSeq* seq)
    {
        for(Square* next = sq_list; next != NULL; next = next->link)
        {
            if(next->instances >= 3)
            {
                // Set the points to the centers of the square
                CvPoint pt[4];
                pt[0].x = (int)next->center_x;
                pt[0].y = (int)(next->center_y - next->size_y/2);
                pt[1].x = (int)(next->center_x - next->size_x/2);
                pt[1].y = (int)next->center_y;
                pt[2].x = (int)next->center_x;
                pt[2].y = (int)(next->center_y + next->size_y/2);
                pt[3].x = (int)(next->center_x + next->size_x/2);
                pt[3].y = (int)next->center_y;

                for(int j = 0; j < 4; j++ )
                { 
                    cvSeqPush(seq, &(pt[j]));
                }

                // Calculate the distance
                next->dist = h_to_dist(next->size_y);
            }
        }
    }

    Square* sq_list;
    int dist;

};

void dumpSeq(CvSeq* s)
{
    // Read through all the existing squares
    CvSeqReader reader;
    int i;
    cvStartReadSeq( s, &reader, 0 );
    for( i = 0; i < (s->total); i++ )
    {
        CvPoint pt;
        CV_READ_SEQ_ELEM( pt, reader );

    }
    return;
}

void sockWrite(SuperSock &s, SquareHolder &sq)
{

    

    // Now that we have the squares we need to generate correct
    // output to the C-RIO.

      static KPacketOut data;
      Square targets[4];
      Square temp_sq;
      int sq_count = 0;    // How many squares did we find?
      
      data.offset_from_top = 0;
      data.offset_from_left = 0;
      data.offset_from_bottom = 0;
      data.offset_from_right = 0;
      data.speed_top = 0;
      data.speed_left = 0;
      data.speed_bottom = 0;
      data.speed_right = 0;
      data.distance = 0;
      data.angle = 0;
      data.target_number = 0;

      // Go through the list of squares
      for(Square* next = sq.sq_list; next != NULL; next = next->link)
      {
          // Ignore if we don't like the quality of the square
          if(next->instances < 3)
              continue;
          // Ignore any squares that are not the right shape.  We
          // know that the proper ratio is 3/4.  If the width is 
          // much wider than the height, we can reject it.
	  if(next->size_x > (2*(next->size_y)))
              continue; 

          // Read this square into a temp square.
          temp_sq = *next;

          // Is this the bottom square?  We can know this if
          // the center is below a certain point (high y value).
          // This point is min_bottom_center.

          if(temp_sq.center_y > min_bottom_center)
          {
              // Check to make sure we don't already have a bottom
              // target.
              if(targets[BOTTOM_IDX].square_id != BOTTOM)
              {
                  // If we don't, move this square into the
                  // bottom target.
                  targets[BOTTOM_IDX] = temp_sq;
                  targets[BOTTOM_IDX].square_id = BOTTOM;
                  data.target_number |= BOTTOM;
                  cout << "BOTTOM = " << targets[BOTTOM_IDX].center_x << "," << targets[BOTTOM_IDX].center_y << endl;
                  sq_count++;
              }
              else
              {
                  // If we do, decide which is better.
                  
                  
              }
          }
          else if(temp_sq.center_y < max_lr_center)
          {
              // Could this be a middle square?
              // if dist < min_top_distance feet we can't really see the
              // top square. But at greater than min_top_distance feet, 
              // the Middle square is limited to a y > 180
              //   
              if(h_to_dist(temp_sq.size_y) > min_top_distance &&
                 temp_sq.center_y < min_y_lr_with_top)
              {
                  // This is a top square
                  targets[TOP_IDX] = temp_sq;
                  targets[TOP_IDX].square_id = TOP;
                  data.target_number |= TOP;
                  cout << "TOP = " << targets[TOP_IDX].center_x << "," << targets[TOP_IDX].center_y << endl;
                  sq_count++;
                  // Is there a bottom square?  If so, does this one have 
                  // the same center?  If so, it is probably a top square.
              }
              else
              {
                  // This is a middle square

                  // Do we have a left square?  If not, move this into the 
                  // left square spot, unless we have a bottom, then we know
                  // where it goes.
                  if(targets[LEFT_IDX].square_id == LEFT)
                  {
                      // There is a left square.  
                      // Is this to the left or right?
                      if(temp_sq.center_x < targets[LEFT_IDX].center_x)
                      {
                          // If left, move the left to right
                          // and move this to left.
                          targets[RIGHT_IDX] = targets[LEFT_IDX];
                          targets[RIGHT_IDX].square_id = RIGHT;
                          cout << "LEFT change to RIGHT = " << targets[RIGHT_IDX].center_x << "," << targets[RIGHT_IDX].center_y << endl;

                          targets[LEFT_IDX] = temp_sq;
                          targets[LEFT_IDX].square_id = LEFT;
                          cout << "LEFT = " << targets[LEFT_IDX].center_x << "," << targets[LEFT_IDX].center_y << endl;
                          sq_count++;
                      }
                      else
                      {
                          // If right, move this to right.
                          targets[RIGHT_IDX] = temp_sq;
                          targets[RIGHT_IDX].square_id = RIGHT;
                          cout << "RIGHT = " << targets[RIGHT_IDX].center_x << "," << targets[RIGHT_IDX].center_y << endl;
                          sq_count++;
                      }
                      // Either way we added a right 
                      data.target_number |= RIGHT;
                  }
                  else
                  {
                      targets[LEFT_IDX] = temp_sq;
                      targets[LEFT_IDX].square_id = LEFT;
                      data.target_number |= LEFT;
                      cout << "LEFT = " << targets[LEFT_IDX].center_x << "," << targets[LEFT_IDX].center_y << endl;
                      sq_count++;
                  }
              }
          }
      }

      printf("Target id = %x\n", data.target_number);

      // Make sure we found at least one square.
      // If not, just send the default values (all 0s)
      if(sq_count > 0)
      {
          // Handle the cases where we only have one
          // square and have to generate the other 3.
          switch(data.target_number)
          {
          case TOP:
              {
                  // Generate the left target
                  targets[LEFT_IDX].center_x = targets[TOP_IDX].center_x - 
                      (targets[TOP_IDX].size_y / target_h_to_lr) /2.0;
                  targets[LEFT_IDX].center_y = targets[TOP_IDX].center_y - 
                      (targets[TOP_IDX].size_y / target_h_to_tb) /2.0;
                  targets[LEFT_IDX].size_x = targets[TOP_IDX].size_x;
                  targets[LEFT_IDX].size_y = targets[TOP_IDX].size_y;
                  targets[LEFT_IDX].square_id = LEFT;

                  // Generate the right target
                  targets[RIGHT_IDX].center_x = targets[TOP_IDX].center_x + 
                      (targets[TOP_IDX].size_y / target_h_to_lr) /2.0;
                  targets[RIGHT_IDX].center_y = targets[TOP_IDX].center_y - 
                       (targets[TOP_IDX].size_y / target_h_to_tb) /2.0;
                  targets[RIGHT_IDX].size_x = targets[TOP_IDX].size_x;
                  targets[RIGHT_IDX].size_y = targets[TOP_IDX].size_y;
                  targets[RIGHT_IDX].square_id = RIGHT;

                  // Generate the bottom target
                  targets[BOTTOM_IDX].center_x = targets[TOP_IDX].center_x;
                  targets[BOTTOM_IDX].center_y = targets[TOP_IDX].center_y - 
                      targets[TOP_IDX].size_y / target_h_to_tb;
                  targets[BOTTOM_IDX].size_x = targets[TOP_IDX].size_x;
                  targets[BOTTOM_IDX].size_y = targets[TOP_IDX].size_y;
                  targets[BOTTOM_IDX].square_id = BOTTOM;

                  break;
              }
          case LEFT:
          case LEFT | RIGHT:
          case LEFT | RIGHT | TOP:
              {
                  // Generate the right target if we don't
                  // already have it.
                  if((data.target_number & RIGHT) == 0)
                  {
                      targets[RIGHT_IDX].center_x = targets[LEFT_IDX].center_x + 
                          targets[LEFT_IDX].size_y / target_h_to_lr;
                      targets[RIGHT_IDX].center_y = targets[LEFT_IDX].center_y;
                      targets[RIGHT_IDX].size_x = targets[LEFT_IDX].size_x;
                      targets[RIGHT_IDX].size_y = targets[LEFT_IDX].size_y;
                      targets[RIGHT_IDX].square_id = RIGHT;
                  }
                  // Generate the top target
                  if((data.target_number & TOP) == 0)
                  {
                      targets[TOP_IDX].center_x = targets[LEFT_IDX].center_x +  
                           (targets[RIGHT_IDX].center_x - targets[LEFT_IDX].center_x) /2.0;
                      targets[TOP_IDX].center_y = targets[LEFT_IDX].center_y + 
                           (targets[LEFT_IDX].size_y / target_h_to_tb) /2.0;
                      targets[TOP_IDX].size_x = targets[LEFT_IDX].size_x;
                      targets[TOP_IDX].size_y = targets[LEFT_IDX].size_y;
                      targets[TOP_IDX].square_id = TOP;
                  }

                  // Generate the bottom target
                  targets[BOTTOM_IDX].center_x = targets[LEFT_IDX].center_x + 
                           (targets[RIGHT_IDX].center_x - targets[LEFT_IDX].center_x) /2.0;
                  targets[BOTTOM_IDX].center_y = targets[LEFT_IDX].center_y - 
                      targets[LEFT_IDX].size_y / target_h_to_tb;
                  targets[BOTTOM_IDX].size_x = targets[LEFT_IDX].size_x;
                  targets[BOTTOM_IDX].size_y = targets[LEFT_IDX].size_y;
                  targets[BOTTOM_IDX].square_id = BOTTOM;

                  break;
              }
          case RIGHT:
          case RIGHT | TOP:
              {
                  // Generate the top target
                  if((data.target_number & TOP) == 0)
                  {
                      targets[TOP_IDX].center_x = targets[RIGHT_IDX].center_x - 
                          (targets[RIGHT_IDX].size_y / target_h_to_lr) /2.0;
                      targets[TOP_IDX].center_y = targets[RIGHT_IDX].center_y + 
                           (targets[RIGHT_IDX].size_y / target_h_to_tb) /2.0;
                      targets[TOP_IDX].size_x = targets[RIGHT_IDX].size_x;
                      targets[TOP_IDX].size_y = targets[RIGHT_IDX].size_y;
                      targets[TOP_IDX].square_id = TOP;
                  }
                  // Generate the left target
                  targets[LEFT_IDX].center_x = targets[RIGHT_IDX].center_x - 
                      targets[RIGHT_IDX].size_y / target_h_to_lr;
                  targets[LEFT_IDX].center_y = targets[RIGHT_IDX].center_y;
                  targets[LEFT_IDX].size_x = targets[RIGHT_IDX].size_x;
                  targets[LEFT_IDX].size_y = targets[RIGHT_IDX].size_y;
                  targets[LEFT_IDX].square_id = LEFT;

                  // Generate the bottom target
                  targets[BOTTOM_IDX].center_x = targets[RIGHT_IDX].center_x - 
                           (targets[RIGHT_IDX].size_y / target_h_to_lr) /2.0;
                  targets[BOTTOM_IDX].center_y = targets[RIGHT_IDX].center_y - 
                      targets[RIGHT_IDX].size_y / target_h_to_tb;
                  targets[BOTTOM_IDX].size_x = targets[RIGHT_IDX].size_x;
                  targets[BOTTOM_IDX].size_y = targets[RIGHT_IDX].size_y;
                  targets[BOTTOM_IDX].square_id = BOTTOM;

                  break;
              }
          case LEFT | TOP:
          case LEFT | BOTTOM:
          case LEFT | BOTTOM | TOP:
              {
                  // See if we need to move the Left into the right

                  // First see if we have a top square
                  if((data.target_number & TOP) && ((data.target_number & BOTTOM) == 0))
                  {
                    if(targets[LEFT_IDX].center_x > targets[TOP_IDX].center_x)
                    {
                      // Swap this puppy
                      targets[RIGHT_IDX] = targets[LEFT_IDX];
                      // Set up our output
                      targets[LEFT_IDX].square_id = 0;
                      data.target_number = RIGHT | TOP | BOTTOM;
                    }
                  // Generate the bottom target
                  targets[BOTTOM_IDX].center_x = targets[TOP_IDX].center_x;
                  targets[BOTTOM_IDX].center_y = targets[TOP_IDX].center_y - 
                      targets[TOP_IDX].size_y / target_h_to_tb;
                  targets[BOTTOM_IDX].size_x = targets[TOP_IDX].size_x;
                  targets[BOTTOM_IDX].size_y = targets[TOP_IDX].size_y;
                  targets[BOTTOM_IDX].square_id = BOTTOM;
                  }
                 else if(data.target_number & BOTTOM)
                 {
                   // position, then fall though to the default.
                   if(targets[LEFT_IDX].center_x > targets[BOTTOM_IDX].center_x)
                   {
                     // Swap this puppy
                     targets[RIGHT_IDX] = targets[LEFT_IDX];
                      
                     // Set up our output
                     targets[LEFT_IDX].square_id = 0;
                     data.target_number = RIGHT | BOTTOM;
                  }
                }
              }
          default:
              {
                  // In this case we always have the bottom
                  // so generate from the bottom target.

                  // Generate the top target
                  if((data.target_number & TOP) == 0)
                  {
                      targets[TOP_IDX].center_x = targets[BOTTOM_IDX].center_x;
                      targets[TOP_IDX].center_y = targets[BOTTOM_IDX].center_y + 
                          targets[BOTTOM_IDX].size_y / target_h_to_tb;
                      targets[TOP_IDX].size_x = targets[BOTTOM_IDX].size_x;
                      targets[TOP_IDX].size_y = targets[BOTTOM_IDX].size_y;
                      targets[TOP_IDX].square_id = TOP;
                  }
                  // Generate the left target
                  if((data.target_number & LEFT) == 0)
                  {
                      targets[LEFT_IDX].center_x = targets[BOTTOM_IDX].center_x - 
                          (targets[BOTTOM_IDX].size_y / target_h_to_lr) /2.0;
                      targets[LEFT_IDX].center_y = targets[BOTTOM_IDX].center_y - 
                          targets[BOTTOM_IDX].size_y / target_h_to_tb;
                      targets[LEFT_IDX].size_x = targets[BOTTOM_IDX].size_x;
                      targets[LEFT_IDX].size_y = targets[BOTTOM_IDX].size_y;
                      targets[LEFT_IDX].square_id = LEFT;
                  }

                  // Generate the right target
                  if((data.target_number & RIGHT) == 0)
                  {
                      targets[RIGHT_IDX].center_x = targets[BOTTOM_IDX].center_x + 
                      (targets[BOTTOM_IDX].size_y / target_h_to_lr) /2.0;
                      targets[RIGHT_IDX].center_y = targets[BOTTOM_IDX].center_y - 
                       (targets[BOTTOM_IDX].size_y / target_h_to_tb) /2.0;
                      targets[RIGHT_IDX].size_x = targets[BOTTOM_IDX].size_x;
                      targets[RIGHT_IDX].size_y = targets[BOTTOM_IDX].size_y;
                      targets[RIGHT_IDX].square_id = RIGHT;
                  }

                  break;
              }
          }

          data.offset_from_top = frame_center_x - targets[TOP_IDX].center_x;
          data.offset_from_left =  frame_center_x - targets[LEFT_IDX].center_x;
          data.offset_from_bottom =  frame_center_x - targets[BOTTOM_IDX].center_x;
          data.offset_from_right =  frame_center_x - targets[RIGHT_IDX].center_x;
          data.speed_top = h_to_speed(targets[TOP_IDX].size_y, TOP_IDX);
          data.speed_left = h_to_speed(targets[LEFT_IDX].size_y, LEFT_IDX);
          data.speed_bottom = h_to_speed(targets[BOTTOM_IDX].size_y, BOTTOM_IDX);
          data.speed_right = h_to_speed(targets[RIGHT_IDX].size_y, RIGHT_IDX);
          float angle = 0.0;
//          for(int i = TOP_IDX; i <= BOTTOM_IDX; i++)
          for(int i = 0; i < 4; i++)
          {
              targets[i].dist = h_to_dist(targets[i].size_y);
              data.distance += targets[i].dist;   // In inches
              float value = target_h_to_w * (targets[i].size_x / targets[i].size_y);
              if(value < 1.0)
                  angle += acosf(value);
          }
          data.distance /= 4.0;
          angle /= 4.0;
          data.angle = (int)((180.0/M_PI) * angle);
          cout << "angle = " << angle << endl;

      }

      data.frame_count++;

      cout << "data.head  = " <<                data.head                 << endl
	       << "data.frame_count = " <<          data.frame_count          << endl
	       << "data.offset_from_top = " <<      data.offset_from_top      << endl
	       << "data.offset_from_left = " <<     data.offset_from_left     << endl
	       << "data.offset_from_right = " <<    data.offset_from_right    << endl
	       << "data.offset_from_bottom = " <<   data.offset_from_bottom   << endl
	       << "data.speed_top = " <<            data.speed_top            << endl
	       << "data.speed_left = " <<           data.speed_left           << endl
	       << "data.speed_right = " <<          data.speed_right          << endl
	       << "data.speed_bottom = " <<         data.speed_bottom         << endl
	       << "data.distance = " <<             data.distance             << endl
	       << "data.angle = " <<                data.angle                << endl
	       << "data.target_number = " << hex << data.target_number << dec << endl;

      s.write_timeout((unsigned char*)&data, sizeof(data), 10000);

      return;

}


// FIRST -- The square finder produces a bunch of squares around
// each found square.  This function will reduce the number to the
// minumum by comparing all the center points and combining all those 
// that are nearer than dist.
//
// @param[in] - storage - place where the reduced set of squares are stored
// @param[in] - all_squares - The original list of squares
// @param[in] - dist - How far appart should the centers be before we consider
//                     it the same point
// @param[out] - sq - the list of squares that we are tracking
// @return - The output list of squares in the format needed for display
CvSeq* reduceSquares(CvMemStorage* storage, CvSeq* all_squares, int dist, SquareHolder &sq)
{
    CvSeq* out_sq = cvCreateSeq( 0, sizeof(CvSeq), sizeof(CvPoint), storage );

    sq.dist = dist;

    // Read through all the existing squares
    CvSeqReader reader;
    int i;
    cvStartReadSeq( all_squares, &reader, 0 );
    for( i = 0; i < (all_squares->total); i += 4 )
    {
        CvPoint pt[4];
        CV_READ_SEQ_ELEM( pt[0], reader );
        CV_READ_SEQ_ELEM( pt[1], reader );
        CV_READ_SEQ_ELEM( pt[2], reader );
        CV_READ_SEQ_ELEM( pt[3], reader );

        // fill in the parameters for this new square
        sq.add_pt(pt);
    }
    
    dumpSeq(all_squares);
    
    sq.fill_seq(out_sq);

    dumpSeq(out_sq);

    return out_sq;
}

} /* namespace Team691 */



#endif // FRC2012_H
