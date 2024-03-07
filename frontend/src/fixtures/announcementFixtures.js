const announcementFixtures = {
    oneAnnouncement: {
        "id": 1,
        "commonsId": 1,
        "start": "2025-03-05T15:50:10",
        "end": "2025-03-05T16:00:00",
        "announcement": "singular announcement"
    },
    threeAnnouncements: [
        {
            "id": 1,
            "commonsId": 1,
            "start": "2022-03-05T15:50:10",
            "end": "2022-03-05T16:50:10",
            "announcement": "common announcement 1"
        },
        {
            "id": 2,
            "commonsId": 1,
            "start": "2012-03-05T15:50:10",
            "end": null,
            "announcement": "common announcement 2 (no end date)"
        },
        {
            "id": 3,
            "commonsId": 1,
            "start": "2026-03-05T15:50:10",
            "end": "2026-03-05T16:50:10",
            "announcement": "common announcement 3"
        }
    ]
};


export { announcementFixtures };