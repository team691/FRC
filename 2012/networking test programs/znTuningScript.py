

def znPIDTuningSomeOvershoot( KU, TU ):
    KP = KU * 0.33
    KI = KP / TU
    KD = KP * TU / 3
    print "for a Ku of ", KU, " and a Tu of ", TU, " the PID constants are: "
    print "KP  :  ", KP
    print "KI  :  ", KI
    print "KD  :  ", KD
    
def znPIDTuningNoOvershoot( KU, TU ):
    KP = KU * 0.2
    KI = KP / TU
    KD = KP * TU / 3
    print "for a Ku of ", KU, " and a Tu of ", TU, " the PID constants are: "
    print "KP  :  ", KP
    print "KI  :  ", KI
    print "KD  :  ", KD
    
def znPIDTuningClassicPID( KU, TU ):
    KP = KU * 0.6
    KI = KP * 2 / TU
    KD = KP * TU / 8
    print "for a Ku of ", KU, " and a Tu of ", TU, " the PID constants are: "
    print "KP  :  ", KP
    print "KI  :  ", KI
    print "KD  :  ", KD
    
def znPIDTuningPessenIntegralRule( KU, TU ):
    KP = KU * 0.7
    KI = KP * 2.5 / TU
    KD = KP * TU * 0.15
    print "for a Ku of ", KU, " and a Tu of ", TU, " the PID constants are: "
    print "KP  :  ", KP
    print "KI  :  ", KI
    print "KD  :  ", KD



con = True
while con == True:
    print "what tuning rule do you want to use?"
    data = str( raw_input("Classic, Pessen, Some, or No? --- (C/P/S/N) >>>    "))
    ku = float( raw_input("What is the ultimate gain, Ku? >>>    ") )
    tu = float( raw_input("What is the ultimate period, in centiseconds Tu? >>>    ") )

    if data == 'C' or data == 'c':
        znPIDTuningClassic(ku, tu)
    elif data == 'P' or data == 'p':
        znPIDTuningPessenIntegralRule(ku,tu)
    elif data == 'S' or data == 's':
        znPIDTuningSomeOvershoot(ku,tu)
    elif data == 'N' or data == 'n':
        znPIDTuningNoOvershoot(ku,tu)
    
    data = str( raw_input("continue? (Y/N) >>>>    ") )
    if data == 'Y' or data == 'y':
        con = True
    else:
        con = False
