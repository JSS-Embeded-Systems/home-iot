import time
import json
import random
import paho.mqtt.client as mqtt

# --- MQTT Setup ---
BROKER = "192.168.200.25"  # Replace with your broker IP or hostname
PORT = 1883
TOPIC = "sensors/sleep"

client = mqtt.Client("sensor_simulator")

def connect():
    print(f"Connecting to MQTT broker at {BROKER}:{PORT}...")
    client.connect(BROKER, PORT)
    print("Connected.")

def generate_fake_data():
    return {
        "presence": random.randint(0, 1),
        "sleepState": random.choice([0, 1, 2]),
        "averageRespiration": random.randint(12, 20),
        "averageHeartbeat": random.randint(50, 80),
        "turnoverNumber": random.randint(0, 5),
        "largeBodyMove": random.randint(0, 2),
        "minorBodyMove": random.randint(0, 4),
        "apneaEvents": random.randint(0, 1)
    }

# --- Main Loop ---
connect()
client.loop_start()

try:
    while True:
        data = generate_fake_data()
        payload = json.dumps(data)
        client.publish(TOPIC, payload)
        print(f"Published to {TOPIC}: {payload}")
        time.sleep(5)  # simulate a 5-second update interval

except KeyboardInterrupt:
    print("\nExiting...")
    client.loop_stop()
    client.disconnect()