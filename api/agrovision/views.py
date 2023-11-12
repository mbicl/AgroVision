from django.shortcuts import render
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .models import User, Token
from firebase_admin import credentials, initialize_app, storage


# cred = credentials.Certificate("YOUR DOWNLOADED CREDENTIALS FILE (JSON)")
# initialize_app(cred, {'storageBucket': 'YOUR FIREBASE STORAGE PATH (without gs://)'})

@api_view(http_method_names=['POST', "GET"])
def login(requests):
    if requests.method == "POST":
        phone = requests.data.get("phone")
        user = User.objects.filter(username=phone)
        if user:
            token = Token.objects.create()
            user.first().tokens.add(token)
            return Response({
                "token": f"{token.token}"
            })
        user = User.objects.create(username=phone)
        token = Token.objects.create()
        user.tokens.add(token)
        user.save()
        return Response({
            "token": f"{token.token}"
        })
    return Response({
        "token": None
    })
    
@api_view(http_method_names=['POST', "GET"])
def upload(request):
    print(request.method)
    if request.method == "POST":
        phone = request.data.get("phone")
        image = request.data.get("image")
        name = request.data.get("name")
        latitude = request.data.get("latitude")
        logitude = request.data.get("logitude")

        import requests
        from main import main
        

        # res = requests.get(image)
        # with open(f"images/{name}", "wb") as f:
        #     f.write(res.content)

        from datetime import datetime, timedelta
        now = datetime.now().date()
        delta = now + timedelta(days=10)
        res = requests.get(
            f"https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/41.3500932,69.2083192/{now}/{delta}?key=6GG2SH4FTGWBXYK8USRJKBSXZ"
        )
        data = res.json()["days"]
        rdata = []
        counter = 0
        while counter < 10:
            rdata.append((round((data[counter]["temp"] - 32) * 5 / 9, 1), data[counter]["humidity"], ))
            print("Tempurature", round((data[counter]["temp"] - 32) * 5 / 9, 1), "Humidity", data[counter]["humidity"])
            counter += 1
        a = main(f"test/{name}", rdata)
        return Response({
            "name": a[0],
            "img1": a[1].replace("test/", ""),
            "img2": a[2].replace("test/", ""),
        })


    return Response({
        "status": "ok"
    })
