from django.urls import path
from . import views

app_name = 'dashboard'

urlpatterns = [
    path('login/', views.login_view, name='login'),
    path('logout/', views.logout_view, name='logout'),
    path('', views.index, name='index'),
    path('products/', views.products, name='products'),
    path('customers/', views.customers, name='customers'),
    path('orders/', views.orders, name='orders'),
]
