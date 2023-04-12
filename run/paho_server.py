import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish   

import json




measure_period_11 = 15
measure_period_12 = 8

def on_connect(mqttc, obj, flags, rc):   
    print("rc: "+str(rc))
def on_message(mqttc, obj, msg):
    global measure_period_11
    global measure_period_12   
    print(msg.topic+" "+str(msg.qos)+" "+str(msg.payload))
    if str(msg.topic).__contains__('sensors') == True:
        if str(msg.payload).__contains__('sensor_list_request')== True:
           message='{"sensors":[{"sensor_id":11,"measure_period":'+str(measure_period_11)+',"lang":52.21879795247924,"long":21.011794125027144,"description":"info about sensor 11"},{"sensor_id":12,"measure_period":'+str(measure_period_12)+',"lang":22,"long":12,"description":"info about sensor 12"}]}' 
           publish.single("app/1/sensors/response",message, hostname="test.mosquitto.org", port=1883)
    if str(msg.topic).__contains__('history') == True:
        if str(msg.payload).__contains__('"sensor_id":"11"')== True:
           message='{"history":[{"sensor_id":1,"timestamp":1674725683,"ph":7,"water_level":150},{"sensor_id":1,"timestamp":1674718483,"ph":8,"water_level":200},{"sensor_id":1,"timestamp":1674722083,"ph":6,"water_level":300},{"sensor_id":1,"timestamp":1674729283,"ph":6.5,"water_level":350}]}'
           publish.single("app/1/history/response",message,hostname="test.mosquitto.org", port=1883)
        if str(msg.payload).__contains__('"sensor_id":"12"')== True:
           message='{"history":[{"sensor_id":2,"timestamp":1674725683,"ph":7,"water_level":150},{"sensor_id":2,"timestamp":1674718483,"ph":8.5,"water_level":230},{"sensor_id":2,"timestamp":1674722083,"ph":7,"water_level":300},{"sensor_id":2,"timestamp":1674729283,"ph":5,"water_level":320}]}'
           publish.single("app/1/history/response",message,hostname="test.mosquitto.org", port=1883)
    if str(msg.topic).__contains__('configuration') == True:
        if str(msg.payload).__contains__('"sensor_id":"11"')== True:
            payload = json.loads(msg.payload)
            measure_period_11 = int(payload['measure_period'])           
        if str(msg.payload).__contains__('"sensor_id":"12"')== True:
            payload = json.loads(msg.payload)
            measure_period_12 = int(payload['measure_period'])
            
            



def on_subscribe(mqttc, obj, mid, granted_qos):   
    print("Subscribed: "+str(mid)+" "+str(granted_qos))
def on_publish(mqttc, obj, mid):
    print("Published with id: "+str(mid))



mqttc = mqtt.Client()
mqttc.on_connect = on_connect
mqttc.on_message = on_message
mqttc.on_subscribe = on_subscribe
mqttc.connect("test.mosquitto.org", 1883, 60)
mqttc.subscribe("app/+/sensors/request", 0)
mqttc.subscribe("app/+/sensors/response", 0)
mqttc.subscribe("app/+/history/request",0)
mqttc.subscribe("sensor/+/configuration",0)
mqttc.subscribe("sensor/+/data",0)
 
mqttc.loop_forever(timeout=1.0, max_packets=5, retry_first_connection=False)
