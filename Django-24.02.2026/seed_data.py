"""
Seed script – run with:
    python manage.py shell < seed_data.py
"""

import os
import django
import random
from datetime import date, timedelta
from decimal import Decimal

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'ecommerce_dashboard.settings')
django.setup()

from dashboard.models import Category, Customer, Product, Order

# ── clear existing data ────────────────────────────────
Order.objects.all().delete()
Product.objects.all().delete()
Customer.objects.all().delete()
Category.objects.all().delete()
print("Cleared existing data.")

# ── categories ────────────────────────────────────────
categories_data = ["Electronics", "Clothing", "Home & Kitchen", "Books", "Sports", "Beauty"]
categories = [Category.objects.create(name=n) for n in categories_data]
print(f"Created {len(categories)} categories.")

# ── products ──────────────────────────────────────────
products_data = [
    ("iPhone 15 Pro",         "Electronics",   999.99,  45),
    ("Samsung Galaxy S24",    "Electronics",   849.99,  30),
    ("Sony WH-1000XM5",       "Electronics",   349.99,  80),
    ("MacBook Air M3",        "Electronics",  1299.99,  20),
    ("Levi's 501 Jeans",      "Clothing",       79.99, 150),
    ("Nike Air Max 270",      "Clothing",      129.99, 200),
    ("Adidas Ultraboost 23",  "Clothing",      179.99, 120),
    ("Winter Puffer Jacket",  "Clothing",       99.99,  60),
    ("Instant Pot Duo 7-in-1","Home & Kitchen",  79.99, 100),
    ("KitchenAid Mixer",      "Home & Kitchen", 399.99,  25),
    ("Dyson V15 Vacuum",      "Home & Kitchen", 749.99,  18),
    ("The Pragmatic Programmer","Books",         49.99, 300),
    ("Clean Code",            "Books",           39.99, 250),
    ("Atomic Habits",         "Books",           24.99, 400),
    ("Yoga Mat Pro",          "Sports",          49.99, 180),
    ("Resistance Band Set",   "Sports",          29.99, 220),
    ("Dumbbell Set 20kg",     "Sports",         119.99,  70),
    ("Cetaphil Moisturiser",  "Beauty",          18.99, 300),
    ("The Ordinary Serum",    "Beauty",          14.99, 350),
    ("Maybelline Mascara",    "Beauty",           9.99, 500),
]

cat_map = {c.name: c for c in categories}
products = []
for name, cat_name, price, stock in products_data:
    p = Product.objects.create(
        name=name, category=cat_map[cat_name],
        price=Decimal(str(price)), stock=stock
    )
    products.append(p)
print(f"Created {len(products)} products.")

# ── customers ─────────────────────────────────────────
customers_data = [
    ("Alice Johnson",   "alice@example.com",   "New York",    "NY"),
    ("Bob Smith",       "bob@example.com",     "Los Angeles", "CA"),
    ("Carol White",     "carol@example.com",   "Chicago",     "IL"),
    ("David Brown",     "david@example.com",   "Houston",     "TX"),
    ("Eva Martinez",    "eva@example.com",     "Phoenix",     "AZ"),
    ("Frank Lee",       "frank@example.com",   "Philadelphia","PA"),
    ("Grace Wilson",    "grace@example.com",   "San Antonio", "TX"),
    ("Henry Taylor",    "henry@example.com",   "San Diego",   "CA"),
    ("Isla Anderson",   "isla@example.com",    "Dallas",      "TX"),
    ("Jack Thomas",     "jack@example.com",    "San Jose",    "CA"),
    ("Karen Harris",    "karen@example.com",   "Austin",      "TX"),
    ("Liam Jackson",    "liam@example.com",    "Jacksonville","FL"),
    ("Mia Martin",      "mia@example.com",     "Columbus",    "OH"),
    ("Noah Garcia",     "noah@example.com",    "Charlotte",   "NC"),
    ("Olivia Clark",    "olivia@example.com",  "Indianapolis","IN"),
]

base_join = date(2024, 1, 1)
customers = []
for i, (name, email, city, state) in enumerate(customers_data):
    c = Customer.objects.create(
        name=name, email=email, city=city, state=state,
        joined_date=base_join + timedelta(days=i * 25)
    )
    customers.append(c)
print(f"Created {len(customers)} customers.")

# ── orders ────────────────────────────────────────────
STATUSES = ['pending', 'processing', 'shipped', 'delivered', 'delivered', 'delivered', 'cancelled']

random.seed(42)
order_count = 0
start_date = date(2025, 3, 1)

for day_offset in range(365):
    order_date = start_date + timedelta(days=day_offset)
    daily = random.randint(1, 5)
    for _ in range(daily):
        customer = random.choice(customers)
        product  = random.choice(products)
        quantity = random.randint(1, 4)
        total    = product.price * quantity
        status   = random.choice(STATUSES)
        Order.objects.create(
            customer=customer, product=product,
            quantity=quantity, total_price=total,
            status=status, order_date=order_date
        )
        order_count += 1

print(f"Created {order_count} orders.")
print("\nSeeding complete! Run: python manage.py runserver")
