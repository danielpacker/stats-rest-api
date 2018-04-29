import requests
import random
import time
import sys

host = "localhost:8080"
endpoint = "transactions"

def make_requests(n, delay=0.5):
    for i in range(n):
        time.sleep(delay)
        amt = float(random.randint(1, 100))
        ts = int(round(time.time() * 1000))
        url = "http://{}/{}".format(host, endpoint)

        json_req = { "amount": amt, "timestamp": ts }
        print("requesting: " + url)
        print(json_req)
        r = requests.post(url, json=json_req)
        print("status code: {}".format(r.status_code))
        try:
            print(r.json())
        except:
            print("no JSON")

if __name__ == "__main__":

    if len(sys.argv) == 2:
        make_requests(int(sys.argv[1]))
    if len(sys.argv) == 3:
        make_requests(int(sys.argv[1]), float(sys.argv[2]))
