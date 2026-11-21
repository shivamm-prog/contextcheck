"""Verification engine and endpoints tests."""

def test_demo_scenario_1_false_recharge(client):
    payload = {
        "content": "Urgent! PM Free Recharge Scheme gives ₹500 free recharge to all Indian users. Click link to claim now!",
        "source_platform": "WhatsApp"
    }
    response = client.post("/verify/text", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["verdict"] == "FALSE"
    assert data["recommended_action"] == "DON'T FORWARD"
    assert len(data["evidence"]) > 0
    assert len(data["sources"]) > 0
    assert data["is_demo"] is True

def test_demo_scenario_2_misleading_context_pune_flood_english(client):
    payload = {
        "content": "Breaking: This video shows today's flooding in Pune. Situation is critical!",
        "claimed_date": "Today",
        "claimed_location": "Pune"
    }
    response = client.post("/verify/text", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["verdict"] == "MISLEADING CONTEXT"
    assert "REAL MEDIA, FALSE CONTEXT" in data["context_comparison"] or "earlier" in data["evidence"][0].lower()
    assert "Today" in data["date_comparison"] or "2019" in data["date_comparison"]
    assert "Pune" in data["location_comparison"]
    assert data["recommended_action"] == "DON'T FORWARD"

def test_demo_scenario_2_multilingual_marathi_mixed(client):
    """Test exact user prompt case: 'हा video पुण्यातला आहे आणि आजचा आहे.'"""
    payload = {
        "content": "हा video पुण्यातला आहे आणि आजचा आहे.",
        "source_platform": "WhatsApp"
    }
    response = client.post("/verify/text", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["verdict"] == "MISLEADING CONTEXT"
    assert "Marathi" in data["language"]
    assert data["is_demo"] is True

def test_demo_scenario_3_unverified_local_rumor(client):
    payload = {
        "content": "Attention all parents: school holiday declared tomorrow due to surprise inspection across city schools.",
        "source_platform": "WhatsApp"
    }
    response = client.post("/verify/text", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["verdict"] == "UNVERIFIED"
    assert data["recommended_action"] == "WAIT FOR OFFICIAL CONFIRMATION"

def test_empty_content_validation(client):
    response = client.post("/verify/text", json={"content": ""})
    assert response.status_code in [400, 422]

def test_image_verification_endpoint(client):
    payload = {
        "caption": "Photo from today's heavy rain and flood in Pune near bus stand.",
        "image_url": "https://example.com/test_flood.jpg",
        "claimed_location": "Pune"
    }
    response = client.post("/verify/image", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["content_type"] == "IMAGE"
    assert data["verdict"] in ["MISLEADING CONTEXT", "UNVERIFIED", "FALSE"]

def test_video_verification_endpoint(client):
    payload = {
        "caption": "Video showing massive waterlogging on highways in Pune today.",
        "video_url": "https://example.com/test_pune_water.mp4"
    }
    response = client.post("/verify/video", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["content_type"] == "VIDEO"
    assert data["verdict"] == "MISLEADING CONTEXT"

def test_audio_verification_endpoint(client):
    payload = {
        "transcript_text": "Forward this audio to everyone. Urgent alert about free government cash transfers.",
        "context_note": "Voice note forwarded in family WhatsApp group"
    }
    response = client.post("/verify/audio", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["content_type"] == "AUDIO"
    assert "verdict" in data
