Android App Head-Pose Wheelchair Controller with MediaPipe

Project Overview
This project proposes a head-pose-based wheelchair control system that uses Google MediaPipe on an Android smartphone and an ESP32 microcontroller. 
The system is designed to assist people with severe motor disabilities (such as quadriplegia) in controlling a motorized wheelchair hands-free using only head movements.
The Android device captures the user head orientation (yaw, pitch, and roll) in real time through the front-facing camera. These angles are then classified into movement commands forward, backward, left, right, or stop which are sent over Bluetooth Serial to the ESP32 to drive DC motors accordingly.
