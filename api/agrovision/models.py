from django.db import models
from django.contrib.auth.models import AbstractUser
from random import choice
from string import ascii_letters, digits

from .manager import UserManager


def tokenize():
    token = ""
    for i in range(10):
        token += choice(ascii_letters)
        token += choice(digits)
    return token


class Token(models.Model):
    token = models.CharField(max_length=20, default=tokenize, editable=False)


class User(AbstractUser):
    username = models.CharField(max_length=20, unique=True, null=False, blank=False, verbose_name="Phone")
    full_name = models.CharField(max_length=100, null=True, blank=True, verbose_name="Full name")
    tokens = models.ManyToManyField(Token, related_name="user_tokens")

    objects = UserManager()

    USERNAME_FIELD = "username"

    def __str__(self):
        return self.username
