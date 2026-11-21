"""Pre-configured Deterministic Demo Scenarios for Demo & Hackathon testing."""
from typing import Dict, Any, Optional

DEMO_SCENARIO_1 = {
    "id": "demo-gov-scheme",
    "name": "False Government Recharge Scheme",
    "trigger_keywords": ["free recharge", "pm scheme", "yojana", "500 recharge", "recharge link", "फ्री रिचार्ज", "योजना"],
    "verdict": "FALSE",
    "confidence": 0.96,
    "claim": "Government is giving free ₹500 mobile recharge to all citizens through an external link under PM Scheme.",
    "evidence": [
        "PIB Fact Check confirmed no such scheme has been launched by the Government of India or Ministry of Communications.",
        "The circulating link points to an unauthorized phishing website designed to harvest phone numbers and OTPs.",
        "Telecom Regulatory Authority of India (TRAI) and official operators have repeatedly warned against such fraudulent messages."
    ],
    "sources": [
        "Press Information Bureau (PIB) Fact Check (Official Ref: PIB-FC-2024-RC)",
        "Ministry of Electronics and Information Technology (MeitY) Cyber Dost Advisory",
        "TRAI Public Warning on Phishing Schemes"
    ],
    "date_comparison": "Claimed: Current active scheme | Evidence: Repeatedly debunked hoax circulating since 2021",
    "location_comparison": "Claimed: Pan-India | Evidence: Fraudulent cyber-phishing campaign",
    "context_comparison": "Deceptive scheme context using official emblems without government authorization",
    "recommended_action": "DON'T FORWARD",
    "is_demo": True
}

DEMO_SCENARIO_2 = {
    "id": "demo-pune-flood",
    "name": "Pune Flood - Real Media with False Context",
    "trigger_keywords": [
        "today's flooding in pune", "flood in pune", "pune flood", 
        "हा video पुण्यातला आहे आणि आजचा आहे", "पुण्यातला पूर", "आजचा पूर", 
        "पुण्यातला आहे", "आजचा आहे", "pune flooding", "pune heavy rain",
        "waterlogging on highways in pune", "flooding in pune"
    ],
    "verdict": "MISLEADING CONTEXT",
    "confidence": 0.94,
    "claim": "This video shows today's catastrophic flooding in Pune.",
    "evidence": [
        "Visual media verification shows the footage is genuine and unedited, but it was recorded during the September 2019 flash floods near Katraj, Pune.",
        "Local meteorological data and current municipal reports confirm normal conditions with no cloudburst or emergency flood warning in Pune today.",
        "Reverse visual search matches identical footage archived on regional news channels on September 26, 2019."
    ],
    "sources": [
        "Maharashtra State Disaster Management Authority Live Bulletin",
        "Regional Meteorological Centre, Pune - Daily Weather Bulletin",
        "Archived Broadcast News Archive (Sept 2019 Incident Archive)"
    ],
    "date_comparison": "Claimed: Today / Current | Evidence: September 26, 2019 (Historical event)",
    "location_comparison": "Claimed: Pune (Current flood) | Evidence: Katraj area (Historical 2019 flood footage)",
    "context_comparison": "REAL MEDIA, FALSE CONTEXT: Genuine flood footage repurposed to falsely depict current weather conditions.",
    "recommended_action": "DON'T FORWARD",
    "is_demo": True
}

DEMO_SCENARIO_3 = {
    "id": "demo-local-rumor",
    "name": "Unverified Local Holiday Rumor",
    "trigger_keywords": ["school holiday tomorrow", "surprise inspection", "holiday declared tomorrow", "शाळांना सुट्टी", "स्कूल छुट्टी"],
    "verdict": "UNVERIFIED",
    "confidence": 0.40,
    "claim": "Local schools and colleges are declared closed tomorrow due to an emergency surprise government inspection.",
    "evidence": [
        "No official circular, gazette, or press release has been issued by the District Collector or Education Department.",
        "Local school administration boards have not issued any closure notice to parents or staff.",
        "Insufficient public records exist to corroborate or conclusively falsify this message."
    ],
    "sources": [
        "District Collector & Magistrate Official Portal (No Circulars Found)",
        "Department of School Education & Sports Official Notice Board"
    ],
    "date_comparison": "Claimed: Tomorrow | Evidence: No scheduled shutdown in official calendar",
    "location_comparison": "Claimed: Local District Schools | Evidence: Uncorroborated local claim",
    "context_comparison": "UNVERIFIED: Circular claims lack official letterhead, reference numbers, or verified department signatures.",
    "recommended_action": "WAIT FOR OFFICIAL CONFIRMATION",
    "is_demo": True
}

ALL_DEMO_SCENARIOS = [DEMO_SCENARIO_1, DEMO_SCENARIO_2, DEMO_SCENARIO_3]

def match_demo_scenario(text: str) -> Optional[Dict[str, Any]]:
    """Match input text against deterministic demo scenarios."""
    if not text:
        return None
    lower_text = text.lower().strip()
    
    for scenario in ALL_DEMO_SCENARIOS:
        for kw in scenario["trigger_keywords"]:
            if kw.lower() in lower_text:
                return scenario
    return None
