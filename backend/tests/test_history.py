"""History endpoints tests."""

def test_history_flow(client):
    # Verify something to populate history
    verify_res = client.post("/verify/text", json={
        "content": "Verify this demo message for history",
        "context_caption": "Test context"
    })
    assert verify_res.status_code == 200
    verified_data = verify_res.json()
    created_id = verified_data.get("id")

    # 1. Fetch history
    history_res = client.get("/history")
    assert history_res.status_code == 200
    items = history_res.json()
    assert isinstance(items, list)
    assert len(items) >= 1

    # 2. Fetch specific item
    target_id = created_id if created_id else items[0]["id"]
    detail_res = client.get(f"/history/{target_id}")
    assert detail_res.status_code == 200
    detail = detail_res.json()
    assert detail["id"] == target_id
    assert "verdict" in detail

    # 3. Delete specific item
    del_res = client.delete(f"/history/{target_id}")
    assert del_res.status_code == 200
    del_data = del_res.json()
    assert del_data["status"] == "success"

    # 4. Clear all history
    clear_res = client.delete("/history")
    assert clear_res.status_code == 200
    assert clear_res.json()["status"] == "success"

    # 5. Verify history is empty
    empty_res = client.get("/history")
    assert empty_res.status_code == 200
    assert len(empty_res.json()) == 0
