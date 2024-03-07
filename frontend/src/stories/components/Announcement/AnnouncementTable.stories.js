import React from 'react';
import AnnouncementTable from "main/components/Announcement/AnnouncementTable";
import { announcementFixtures } from 'fixtures/announcementFixtures';
import { currentUserFixtures } from 'fixtures/currentUserFixtures';
import { rest } from "msw";

export default {
    title: 'components/Announcement/AnnouncementTable',
    component: AnnouncementTable
};

const Template = (args) => {
    return (
        <AnnouncementTable {...args} />
    )
};

export const Empty = Template.bind({});

Empty.args = {
    announcements: []
};

export const ThreeAnnouncementsOrdinaryUser = Template.bind({});

ThreeAnnouncementsOrdinaryUser.args = {
    announcements: announcementFixtures.threeAnnouncements,
    currentUser: currentUserFixtures.userOnly,
};

export const ThreeAnnouncementsAdminUser = Template.bind({});
ThreeAnnouncementsAdminUser.args = {
    announcements: announcementFixtures.threeAnnouncements,
    currentUser: currentUserFixtures.adminUser,
}

ThreeAnnouncementsAdminUser.parameters = {
    msw: [
        rest.delete('/api/announcements', (req, res, ctx) => {
            window.alert("DELETE: " + JSON.stringify(req.url));
            return res(ctx.status(200),ctx.json({}));
        }),
    ]
};