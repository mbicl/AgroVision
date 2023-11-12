from django.urls import path
from .views import (
    login,
    upload,
)

urlpatterns = [
    path('login/', login, name='login'),
    path('upload/', upload, name='upload'),
]