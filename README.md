# WaterCheck

## Overview

WaterCheck is a project focused on measuring water level and ph values in rivers, controlling sensors digitally, storing data from them and visualising this data.

It was created by Adam Pomorski, Michał Ciesielski, Julia Dasiewicz, Karol Duszczyk on PBL5 classes (winter 2022) as a part of the Internet of Things studies at the Warsaw University of Technology.

Part of this project was creating mobile application with given features:
- visualising current measurements from sensors
- alarming user when the set limits of water level and ph are reached
- controlling the measurement periods of sensors
- visualising historical data from sensors

## Application views

![Home](img/home_screen.png)

Home screen layout consist of logo of the project and 4 buttons that are redirecting to the rest of the main views in the application.

![Sensors](img/sensors.png)

Sensors view presents the list of currently working sensors with their record number, id, current ph and water level values. On the right hand side of each row there is a "Edit" button which leads to the settings section of particular sensor

![Settings](img/settings.png)

Settings view presents sensor's record number and description at the top. Then below these section there are several values that are possible to change( by pressing confirm button at the bottom): measure period, max/min ph and water level. Measure period value is sent to the server after confirmation and then all the values are stored in application's internal memory so that each user can have individual values of alarms.

![Map](img/map.png)

Maps view is working on the Google Maps API where each sensor has its location on the map. By clicking the marker u are redirected to the settings section of particular sensor. Currently this feature is not working because the free trial in Google Cloud has expired so to not generate additional costs this feature has been closed. Although it is really easy to attach it to individual Google Cloud Account by generating API KEY and adding it to AndroidManifest.xml.

![Alarms](img/alarms.png)

Alarms view presents the list of alarms that occured from the start of application.

![History](img/history1.png)

![History](img/history2.png)

History view presents the menu to choose particular id and period of time from which the user would like to view data. Underneath this section there are two charts which refresh every time the confirm button is pressed. 

## How to use it

As the application was a part of bigger project it was highly correlated with the server and database which are currently not maintained. Because of that to run this application there are certain solutions implemented in the code to emulate the server repsonses. In the near future there will be added two clients that will emulate sensor measurements and  server operations so that all application features (except for the map view) can be emulated.