from django.shortcuts import render, get_object_or_404, redirect
from django.db.models import Sum, Count, Avg
from django.db.models.functions import TruncMonth
from .models import Product, Customer, Order, Category
import json
from decimal import Decimal
from django.contrib.auth.decorators import login_required
from django.contrib.auth.forms import AuthenticationForm
from django.contrib.auth import login as auth_login, logout as auth_logout
from django.contrib import messages


@login_required
def index(request):
    """Main analytics dashboard view."""
    # KPI Cards
    total_revenue = Order.objects.filter(status='delivered').aggregate(
        total=Sum('total_price'))['total'] or Decimal('0')
    total_orders = Order.objects.count()
    total_customers = Customer.objects.count()
    total_products = Product.objects.count()

    # Revenue over last 12 months (monthly)
    monthly_revenue = (
        Order.objects
        .filter(status='delivered')
        .annotate(month=TruncMonth('order_date'))
        .values('month')
        .annotate(revenue=Sum('total_price'))
        .order_by('month')
    )
    revenue_labels = [r['month'].strftime('%b %Y') for r in monthly_revenue]
    revenue_data = [float(r['revenue']) for r in monthly_revenue]

    # Orders by status
    status_counts = Order.objects.values('status').annotate(count=Count('id'))
    status_labels = [s['status'].capitalize() for s in status_counts]
    status_data = [s['count'] for s in status_counts]

    # Top 5 selling products by revenue
    top_products = (
        Order.objects
        .values('product__name')
        .annotate(revenue=Sum('total_price'))
        .order_by('-revenue')[:5]
    )
    product_labels = [p['product__name'] for p in top_products]
    product_data = [float(p['revenue']) for p in top_products]

    # Sales by category
    category_sales = (
        Order.objects
        .values('product__category__name')
        .annotate(revenue=Sum('total_price'))
        .order_by('-revenue')
    )
    category_labels = [c['product__category__name'] for c in category_sales]
    category_data = [float(c['revenue']) for c in category_sales]

    # Recent 10 orders
    recent_orders = Order.objects.select_related('customer', 'product').order_by('-order_date')[:10]

    chart_payload = json.dumps({
        'revenue_labels': revenue_labels,
        'revenue_data': revenue_data,
        'status_labels': status_labels,
        'status_data': status_data,
        'product_labels': product_labels,
        'product_data': product_data,
        'category_labels': category_labels,
        'category_data': category_data,
    })

    context = {
        'total_revenue': total_revenue,
        'total_orders': total_orders,
        'total_customers': total_customers,
        'total_products': total_products,
        "revenue_labels": revenue_labels,
        "revenue_data": revenue_data,
        "status_labels": status_labels,
        "status_data": status_data,
        "product_labels": product_labels,
        "product_data": product_data,
        "category_labels": category_labels,
        "category_data": category_data,
        'recent_orders': recent_orders,
        'chart_payload': chart_payload,
    }
    return render(request, 'dashboard/index.html', context)


@login_required
def products(request):
    """Products listing with analytics."""
    product_list = Product.objects.select_related('category').annotate(
        total_sold=Sum('order__quantity'),
        total_revenue=Sum('order__total_price')
    ).order_by('-total_revenue')

    category_filter = request.GET.get('category', '')
    if category_filter:
        product_list = product_list.filter(category__name=category_filter)

    categories = Category.objects.all()
    context = {
        'products': product_list,
        'categories': categories,
        'selected_category': category_filter,
    }
    return render(request, 'dashboard/products.html', context)


@login_required
def customers(request):
    """Customers listing with order stats."""
    customer_list = Customer.objects.annotate(
        order_count=Count('order'),
        total_spent=Sum('order__total_price')
    ).order_by('-total_spent')

    context = {'customers': customer_list}
    return render(request, 'dashboard/customers.html', context)


@login_required
def orders(request):
    """Orders listing with filter."""
    status_filter = request.GET.get('status', '')
    order_list = Order.objects.select_related('customer', 'product').order_by('-order_date')
    if status_filter:
        order_list = order_list.filter(status=status_filter)

    context = {
        'orders': order_list,
        'status_choices': Order.STATUS_CHOICES,
        'selected_status': status_filter,
    }
    return render(request, 'dashboard/orders.html', context)


def login_view(request):
    """Simple admin-only login for the dashboard."""
    if request.user.is_authenticated:
        return redirect('dashboard:index')

    form = AuthenticationForm(request, data=request.POST or None)
    for field in form.fields.values():
        field.widget.attrs.setdefault('class', 'form-input')
    if request.method == 'POST' and form.is_valid():
        user = form.get_user()
        if user.is_staff or user.is_superuser:
            auth_login(request, user)
            return redirect('dashboard:index')
        messages.error(request, 'This dashboard is restricted to admin users only.')

    return render(request, 'dashboard/login.html', {'form': form})


@login_required
def logout_view(request):
    """Log out the current admin user."""
    auth_logout(request)
    return redirect('dashboard:login')
