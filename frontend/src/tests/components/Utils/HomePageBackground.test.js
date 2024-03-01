import getBackgroundImage from "main/components/Utils/HomePageBackground";


describe("HomePageBackground tests", () => {
    test("expect correct morning background", () => {

        for(let i = 6; i < 9; i++) {
            expect(getBackgroundImage(i,1)).toEqual('HomePageBackground-morning.png');
        }
        for(let i = 6; i < 9; i++) {
            expect(getBackgroundImage(i,0)).toEqual('HomePageBackground-morning.jpg');
        }
    });

    test("expect correct day background", () => {

        for (let i = 9; i < 18; i++) {
            expect(getBackgroundImage(i,1)).toEqual('HomePageBackground-day.png');
        }

        for (let i = 9; i < 18; i++) {
            expect(getBackgroundImage(i,0)).toEqual('HomePageBackground-day.jpg');
        }
    });

    test("expect correct evening background", () => {

        for (let i = 18; i < 21; i++) {
            expect(getBackgroundImage(i,1)).toEqual('HomePageBackground-evening.png');
        }

        for (let i = 18; i < 21; i++) {
            expect(getBackgroundImage(i,0)).toEqual('HomePageBackground-evening.jpg');
        }
    });

    test("expect correct night background", () => {

        for (let i = 21; i < 24; i++) {
            expect(getBackgroundImage(i,1)).toEqual('HomePageBackground-night.png');
        }

        for (let i = 0; i < 6; i++) {
            expect(getBackgroundImage(i,1)).toEqual('HomePageBackground-night.png');
        }
        for (let i = 21; i < 24; i++) {
            expect(getBackgroundImage(i,0)).toEqual('HomePageBackground-night.jpg');
        }

        for (let i = 0; i < 6; i++) {
            expect(getBackgroundImage(i,0)).toEqual('HomePageBackground-night.jpg');
        }
    });
});