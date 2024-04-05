# WaterCheck

## Overview

WaterCheck is a project focused on measuring water level and pH values in rivers, controlling sensors digitally, storing data from them, and visualizing this data.

It was created by Adam Pomorski, Michał Ciesielski, Julia Dasiewicz, Karol Duszczyk during PBL5 classes (winter 2022) as a part of the Internet of Things studies at the Warsaw University of Technology.

Part of this project was creating a mobile application with the following features:
- Visualizing current measurements from sensors
- Alerting the user when the set limits of water level and pH are reached
- Controlling the measurement periods of sensors
- Visualizing historical data from sensors

## Application views

<div style="text-align:center">
    <img src="img/home_screen.png" alt="Home screen" width="300"/>
</div>

Home screen layout consists of the project logo and four buttons that redirect to the main views in the application.

<div style="text-align:center">
    <img src="img/sensors.png" alt="Sensors" width="300"/>
</div>

Sensors view presents the list of currently working sensors with their record number, ID, current pH, and water level values. Each row includes an "Edit" button leading to the settings section of a particular sensor.

<div style="text-align:center">
    <img src="img/settings.png" alt="Settings" width="300"/>
</div>

Settings view presents sensor's record number and description at the top, followed by values that are possible to change after confirmation: measurement period, max/min pH, and water level. These values are stored in the application's internal memory.

<div style="text-align:center">
    <img src="img/map.png" alt="Map" width="300"/>
</div>

Maps view utilizes the Google Maps API where each sensor has its location marked. Clicking on a marker redirects to the settings section of a particular sensor. This feature is currently disabled due to the expiration of the free trial in Google Cloud.

<div style="text-align:center">
    <img src="img/alarms.png" alt="Alarms" width="300"/>
</div>

Alarms view presents the list of alarms that occurred since the start of the application.

<div style="text-align:center">
    <img src="img/history1.png" alt="History" width="300"/>
    <img src="img/history2.png" alt="History" width="300"/>
</div>

History view allows users to choose a particular ID and time period to view data. Two charts below refresh every time the confirm button is pressed.

## How to use it

### Server emulation

As the application was a part of a bigger project, it was highly correlated with the server and database, which are currently not maintained. To run this application, two simple MQTT Clients were created using the Paho library in Python. `paho_server.py` responds to application requests such as sensor list request, history request, and changing measure period request. `paho_publish.py` sends pseudo-random emulated data from sensors to the MQTT Broker.

### Limitations

In this version of the project, the clients only partially emulate some features of the server:
- Sensors:
  - There are only two sensors set in `paho_server`.
  - Data is only emulated for one sensor.
- Settings:
  - It is not possible to change the measure period of a given sensor as it is statically set in `paho_publish`.
- History:
  - Timestamps are not being checked, and for each sensor, there are always the same data points represented on the graphs.

### Running the project

There are two options to run the application:
- Open the `/application` folder in Android Studio and run it from the GUI.
- Download the `WaterCheck.apk` file from the `/run` folder and run the application on a mobile device.

To run Python clients, you need:
- Python 3 (e.g., version 3.9.13)
- Python library `paho-mqtt`
