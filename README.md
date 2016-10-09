# MagicMatrix
A controller app for an 8x8 RGB-matrix powered by a Colorduino or Arduino with Itead Studio Colors Shield.

Example instructions on creating a big 8x8 RGB-matrix with individual LEDs: http://www.instructables.com/id/Lampduino-an-8x8-RGB-Floor-Lamp/?ALLSTEPS
To get your matrix up and running you need two programs: 

1. The Slave-software that runs on the Colorduino/Arduino
2. The Java-based host software (= this Git repository)

##Slave software
The Slave software is written by Lincomatic and needs to be installed on your Colorduino/Arduino board and can be downloaded [here](http://www.instructables.com/files/orig/F73/WPXO/GLJUVVD1/F73WPXOGLJUVVD1.zip). More explanation can be found [over here](http://www.instructables.com/id/Lampduino-an-8x8-RGB-Floor-Lamp/?ALLSTEPS#step17).
###Troubleshooting
To ensure correct working of the slave software, it should be uploaded to the board **with the [Arduino 022 software, which can be found on this page.](https://www.arduino.cc/en/Main/OldSoftwareReleases)** This has something to do with a change in the size of the serial buffer size. 
##Host software
Download the [JAR-file from this repository](https://github.com/wietsebuseyne/MagicMatrix/releases/) and run on your computer after connecting your matrix.
