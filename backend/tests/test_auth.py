"""Authentication endpoint tests."""

def test_register_and_login_flow(client):
    # 1. Register new user
    reg_payload = {
        "username": "shivam_test",
        "email": "shivam@contextcheck.io",
        "password": "Password123!",
        "full_name": "Shivam Test"
    }
    reg_res = client.post("/auth/register", json=reg_payload)
    assert reg_res.status_code == 201
    reg_data = reg_res.json()
    assert "access_token" in reg_data
    assert reg_data["username"] == "shivam_test"
    token = reg_data["access_token"]

    # 2. Duplicate registration fails
    dup_res = client.post("/auth/register", json=reg_payload)
    assert dup_res.status_code == 400

    # 3. Login with credentials
    login_payload = {
        "username": "shivam_test",
        "password": "Password123!"
    }
    login_res = client.post("/auth/login", json=login_payload)
    assert login_res.status_code == 200
    login_data = login_res.json()
    assert "access_token" in login_data

    # 4. Login with invalid password fails
    bad_login = client.post("/auth/login", json={"username": "shivam_test", "password": "WrongPassword"})
    assert bad_login.status_code == 401

    # 5. Access profile with token
    headers = {"Authorization": f"Bearer {token}"}
    profile_res = client.get("/auth/me", headers=headers)
    assert profile_res.status_code == 200
    profile_data = profile_res.json()
    assert profile_data["username"] == "shivam_test"
    assert profile_data["email"] == "shivam@contextcheck.io"

def test_unauthenticated_me_fails(client):
    res = client.get("/auth/me")
    assert res.status_code == 401
