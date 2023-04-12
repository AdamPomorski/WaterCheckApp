import paho.mqtt.publish as publish  
import random
import json
import time
sensor_id = 11
measure_period = 15
while True:
         water_level = random.randint(30,120)
         ph = round(random.uniform(3.5,10.5),1)
         payload = {"measurement":[{"water_level": water_level, "ph": ph, "timestamp": round(time.time()),"sensor_id": sensor_id}]}
         payload = json.dumps(payload)
         publish.single("sensor/"+str(sensor_id)+"/data",payload,hostname="test.mosquitto.org", port=1883)
         time.sleep(measure_period)