from django.contrib import admin
from django.contrib.auth.admin import UserAdmin

from .forms import (
    UserChangeForm,
    UserCreationForm
)
from .models import User


class TeacherAdmin(UserAdmin):
    add_form = UserCreationForm
    form = UserChangeForm
    model = User
    list_display = ("username", "full_name", )
    fieldsets = (
        ("Fermerni tahrirlash", {"fields": ("username", "full_name", "tokens")}),
    )
    add_fieldsets = (
        ("Yangi fermer qo'shish", {
            "classes": ("wide", ),
            "fields": (
                "username",
                "password1", "password2",
                "full_name",
            ),
        }),
    )
    search_fields = ("username", "full_name", )
    ordering = ("username",)

admin.site.register(User, TeacherAdmin)