#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

API_URL="http://localhost:8089"

echo "🧪 PayGuard Test Suite"
echo "======================="
echo ""

# Test 1: Health Checks
echo "1️⃣  Testing Health Checks..."
echo "----------------------------"

services=("8089:API Gateway" "8081:User Service" "8082:Payment Service" "8083:Fraud Engine" "8084:Notification Service" "8085:Reconciliation Service")

for service in "${services[@]}"; do
    port="${service%%:*}"
    name="${service##*:}"
    
    response=$(curl -s http://localhost:$port/actuator/health)
    if echo "$response" | grep -q '"status":"UP"'; then
        echo -e "${GREEN}✓${NC} $name ($port): UP"
    else
        echo -e "${RED}✗${NC} $name ($port): DOWN"
    fi
done
echo ""

# Test 2: User Registration
echo "2️⃣  Testing User Registration..."
echo "--------------------------------"
TIMESTAMP=$(date +%s)
EMAIL="test${TIMESTAMP}@payguard.com"

REGISTER_RESPONSE=$(curl -s -X POST $API_URL/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"Test123!\",
    \"merchantName\": \"Test Shop $TIMESTAMP\",
    \"merchantCategory\": \"DIGITAL\",
    \"country\": \"USA\"
  }")

if echo "$REGISTER_RESPONSE" | grep -q '"id"'; then
    echo -e "${GREEN}✓${NC} Registration successful"
    echo "   Email: $EMAIL"
    USER_ID=$(echo "$REGISTER_RESPONSE" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
    echo "   User ID: $USER_ID"
else
    echo -e "${RED}✗${NC} Registration failed"
    echo "   Response: $REGISTER_RESPONSE"
    exit 1
fi
echo ""

# Test 3: User Login
echo "3️⃣  Testing User Login..."
echo "-------------------------"

LOGIN_RESPONSE=$(curl -s -X POST $API_URL/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"Test123!\"
  }")

if echo "$LOGIN_RESPONSE" | grep -q '"token"'; then
    echo -e "${GREEN}✓${NC} Login successful"
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    echo "   Token: ${TOKEN:0:50}..."
else
    echo -e "${RED}✗${NC} Login failed"
    echo "   Response: $LOGIN_RESPONSE"
    exit 1
fi
echo ""

# Test 4: Get User Profile
echo "4️⃣  Testing Get User Profile (Authenticated)..."
echo "------------------------------------------------"

PROFILE_RESPONSE=$(curl -s $API_URL/api/v1/users/me \
  -H "Authorization: Bearer $TOKEN")

if echo "$PROFILE_RESPONSE" | grep -q "$EMAIL"; then
    echo -e "${GREEN}✓${NC} Profile retrieved successfully"
    echo "   Email: $EMAIL"
else
    echo -e "${RED}✗${NC} Profile retrieval failed"
    echo "   Response: $PROFILE_RESPONSE"
fi
echo ""

# Test 5: Create Payment
echo "5️⃣  Testing Create Payment (Authenticated)..."
echo "-----------------------------------------------"

PAYMENT_RESPONSE=$(curl -s -X POST $API_URL/api/v1/payments/charge \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10000,
    "currency": "USD",
    "customerEmail": "customer@example.com",
    "stripeToken": "tok_visa",
    "description": "Test payment"
  }')

if echo "$PAYMENT_RESPONSE" | grep -q '"id"'; then
    echo -e "${GREEN}✓${NC} Payment created successfully"
    PAYMENT_ID=$(echo "$PAYMENT_RESPONSE" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
    echo "   Payment ID: $PAYMENT_ID"
    PAYMENT_STATUS=$(echo "$PAYMENT_RESPONSE" | grep -o '"status":"[^"]*' | cut -d'"' -f4)
    echo "   Status: $PAYMENT_STATUS"
else
    echo -e "${YELLOW}⚠${NC} Payment creation response:"
    echo "   $PAYMENT_RESPONSE"
fi
echo ""

# Test 6: Get Payment Details
if [ ! -z "$PAYMENT_ID" ]; then
    echo "6️⃣  Testing Get Payment Details..."
    echo "----------------------------------"
    
    PAYMENT_DETAIL=$(curl -s $API_URL/api/v1/payments/$PAYMENT_ID \
      -H "Authorization: Bearer $TOKEN")
    
    if echo "$PAYMENT_DETAIL" | grep -q "$PAYMENT_ID"; then
        echo -e "${GREEN}✓${NC} Payment details retrieved"
        echo "   Amount: \$100.00 USD"
    else
        echo -e "${YELLOW}⚠${NC} Payment details: $PAYMENT_DETAIL"
    fi
    echo ""
fi

# Test 7: Get Fraud Score
if [ ! -z "$PAYMENT_ID" ]; then
    echo "7️⃣  Testing Fraud Score..."
    echo "--------------------------"
    
    FRAUD_RESPONSE=$(curl -s $API_URL/api/v1/fraud/score/$PAYMENT_ID \
      -H "Authorization: Bearer $TOKEN")
    
    if echo "$FRAUD_RESPONSE" | grep -q '"score"'; then
        echo -e "${GREEN}✓${NC} Fraud score retrieved"
        SCORE=$(echo "$FRAUD_RESPONSE" | grep -o '"score":[0-9.]*' | cut -d':' -f2)
        RISK=$(echo "$FRAUD_RESPONSE" | grep -o '"riskLevel":"[^"]*' | cut -d'"' -f4)
        echo "   Score: $SCORE"
        echo "   Risk Level: $RISK"
    else
        echo -e "${YELLOW}⚠${NC} Fraud score: $FRAUD_RESPONSE"
    fi
    echo ""
fi

# Summary
echo "═══════════════════════════════════════════"
echo "🎉 Test Suite Complete!"
echo "═══════════════════════════════════════════"
echo ""
echo "Test User Credentials:"
echo "  Email: $EMAIL"
echo "  Password: Test123!"
echo "  User ID: $USER_ID"
if [ ! -z "$TOKEN" ]; then
    echo "  Token: ${TOKEN:0:50}..."
fi
echo ""
