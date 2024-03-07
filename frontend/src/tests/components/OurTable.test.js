import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import OurTable, { ButtonColumn, DateColumn, PlaintextColumn} from "main/components/OurTable";

describe("OurTable tests", () => {
    const threeRows = [
        {
            col1: 'Hello',
            col2: 'World',
            createdAt: '2021-04-01T04:00:00.000',
            log: "foo\nbar\n  baz",
        },
        {
            col1: 'react-table',
            col2: 'rocks',
            createdAt: '2022-01-04T14:00:00.000',
            log: "foo\nbar",

        },
        {
            col1: 'whatever',
            col2: 'you want',
            createdAt: '2023-04-01T23:00:00.000',
            log: "bar\n  baz",
        }
    ];
    const clickMeCallback = jest.fn();

    const twentyRows = [
        {col1: "react-table 1", col2: "rocks 1", createdAt: "2024-01-04T14:00:00.000", log: "foo\nbar 1"},
        {col1: "whatever 2", col2: "you want 2", createdAt: "2025-04-01T23:00:00.000", log: "bar\n  baz 2"},
        {col1: "Hello 3", col2: "World 3", createdAt: "2024-04-01T04:00:00.000", log: "foo\nbar\n  baz 3"},
        {col1: "react-table 4", col2: "rocks 4", createdAt: "2026-01-04T14:00:00.000", log: "foo\nbar 4"},
        {col1: "whatever 5", col2: "you want 5", createdAt: "2028-04-01T23:00:00.000", log: "bar\n  baz 5"},
        {col1: "react-table 6", col2: "World 6", createdAt: "2027-04-01T04:00:00.000", log: "foo\nbar\n  baz 6"},
        {col1: "react-table 7", col2: "rocks 7", createdAt: "2029-01-04T14:00:00.000", log: "foo\nbar 7"},
        {col1: "whatever 8", col2: "you want 8", createdAt: "2031-04-01T23:00:00.000", log: "bar\n  baz 8"},
        {col1: "Hello 9", col2: "World 9", createdAt: "2030-04-01T04:00:00.000", log: "foo\nbar\n  baz 9"},
        {col1: "react-table 10", col2: "rocks 10", createdAt: "2022-01-04T14:00:00.000", log: "foo\nbar 10"},
        {col1: "whatever 11", col2: "you want 11", createdAt: "2024-04-01T23:00:00.000", log: "bar\n  baz 11"},
        {col1: "Hello 12", col2: "World 12", createdAt: "2025-04-01T04:00:00.000", log: "foo\nbar\n  baz 12"},
        {col1: "react-table 13", col2: "rocks 13", createdAt: "2025-01-04T14:00:00.000", log: "foo\nbar 13"},
        {col1: "whatever 14", col2: "you want 14", createdAt: "2027-04-01T23:00:00.000", log: "bar\n  baz 14"},
        {col1: "Hello 15", col2: "World 15", createdAt: "2026-04-01T04:00:00.000", log: "foo\nbar\n  baz 15"},
        {col1: "react-table 16", col2: "rocks 16", createdAt: "2028-01-04T14:00:00.000", log: "foo\nbar 16"},
        {col1: "whatever 17", col2: "you want 17", createdAt: "2030-04-01T23:00:00.000", log: "bar\n  baz 17"},
        {col1: "Hello 18", col2: "World 18", createdAt: "2029-04-01T04:00:00.000", log: "foo\nbar\n  baz 18"},
        {col1: "react-table 19", col2: "rocks 19", createdAt: "2031-01-04T14:00:00.000", log: "foo\nbar 19"},
        {col1: "whatever 20", col2: "you want 20", createdAt: "2030-01-04T14:00:00.000", log: "foo\nbar 20"}
    ];

    const columns = [
        {
            Header: 'Column 1',
            accessor: 'col1', // accessor is the "key" in the data
        },
        {
            Header: 'Column 2',
            accessor: 'col2',
        },
        ButtonColumn("Click", "primary", clickMeCallback, "testId"),
        DateColumn("Date", (cell) => cell.row.original.createdAt),
        PlaintextColumn("Log", (cell) => cell.row.original.log),
    ];

    test("renders an empty table without crashing", () => {
        render(
            <OurTable columns={columns} data={[]} />
        );
    });

    test("renders a table with two rows without crashing", () => {
        render(
            <OurTable columns={columns} data={threeRows} />
        );
    });

    test("The button appears in the table", async () => {
        render(
            <OurTable columns={columns} data={threeRows} />
        );

        expect(await screen.findByTestId("testId-cell-row-0-col-Click-button")).toBeInTheDocument();
        const button = screen.getByTestId("testId-cell-row-0-col-Click-button");
        fireEvent.click(button);
        await waitFor(() => expect(clickMeCallback).toBeCalledTimes(1));
    });

    test("default testid is testId", async () => {
        render(
            <OurTable columns={columns} data={threeRows} />
        );
        expect(await screen.findByTestId("testid-header-col1")).toBeInTheDocument();
    });

    test("click on a header and a sort caret should appear", async () => {
        render(
            <OurTable columns={columns} data={threeRows} testid={"sampleTestId"} />
        );

        expect(await screen.findByTestId("sampleTestId-header-col1")).toBeInTheDocument();
        const col1Header = screen.getByTestId("sampleTestId-header-col1");

        const col1SortCarets = screen.getByTestId("sampleTestId-header-col1-sort-carets");
        expect(col1SortCarets).toHaveTextContent('');

        const col1Row0 = screen.getByTestId("sampleTestId-cell-row-0-col-col1");
        expect(col1Row0).toHaveTextContent("Hello");

        fireEvent.click(col1Header);
        expect(await screen.findByText("🔼")).toBeInTheDocument();

        fireEvent.click(col1Header);
        expect(await screen.findByText("🔽")).toBeInTheDocument();
    });

    test("pagination controls are visible with twentyRows", () => {
        render(<OurTable columns={columns} data={twentyRows} pageSize={5} />);
        expect(screen.getByText('<<')).toBeInTheDocument();
        expect(screen.getByText('<')).toBeInTheDocument();
        expect(screen.getByText('>')).toBeInTheDocument();
        expect(screen.getByText('>>')).toBeInTheDocument();
    });

    test("navigates through pages correctly with twentyRows", async () => {
        render(<OurTable columns={columns} data={twentyRows} pageSize={5} />);
        const nextPageButton = screen.getByText('>');
        const prevPageButton = screen.getByText('<');

        // Assuming the order of rows in twentyRows, check for specific content
        expect(screen.getByText('react-table 1')).toBeInTheDocument(); // First item on first page

        // Navigate through the pages
        fireEvent.click(nextPageButton); // Move to second page
        expect(screen.getByText('react-table 6')).toBeInTheDocument(); // Check content of the second page

        fireEvent.click(prevPageButton); // Move back to first page
        expect(screen.getByText('react-table 1')).toBeInTheDocument(); // Verify first page content again
    });

    test("first and last page buttons work with twentyRows", () => {
        render(<OurTable columns={columns} data={twentyRows} pageSize={5} />);
        const firstPageButton = screen.getByText('<<');
        const lastPageButton = screen.getByText('>>');

        // Go to last page and check
        fireEvent.click(lastPageButton);
        expect(screen.getByText('react-table 16')).toBeInTheDocument(); // Example content from last page

        // Go back to first page and verify
        fireEvent.click(firstPageButton);
        expect(screen.getByText('react-table 1')).toBeInTheDocument(); // First item's content
    });
    test("page index displays correctly for next and prev buttons", () => {
        render(<OurTable columns={columns} data={twentyRows} pageSize={5} />);
        const nextPageButton = screen.getByText('>');
        const prevPageButton = screen.getByText('<');
        expect(screen.getByTestId("testId-pagination")).toHaveTextContent('1 of 4')
        // next page
        fireEvent.click(nextPageButton);
        expect(screen.getByTestId("testId-pagination")).toHaveTextContent('2 of 4')
        fireEvent.click(nextPageButton);
        expect(screen.getByTestId("testId-pagination")).toHaveTextContent('3 of 4')
        fireEvent.click(nextPageButton);
        expect(screen.getByTestId("testId-pagination")).toHaveTextContent('4 of 4')
        // prev page
        fireEvent.click(prevPageButton);
        expect(screen.getByTestId("testId-pagination")).toHaveTextContent('3 of 4')
        fireEvent.click(prevPageButton);
        expect(screen.getByTestId("testId-pagination")).toHaveTextContent('2 of 4')
        fireEvent.click(prevPageButton);
        expect(screen.getByTestId("testId-pagination")).toHaveTextContent('1 of 4')
    });

    test("next page button is disabled on the last page with twentyRows", () => {
        render(<OurTable columns={columns} data={twentyRows} pageSize={5} />);
        const nextPageButton = screen.getByText('>');
        // Navigate to last page
        fireEvent.click(screen.getByText('>>'));
        expect(nextPageButton).toBeDisabled();
    });

    test("previous page button is disabled on the first page with twentyRows", () => {
        render(<OurTable columns={columns} data={twentyRows} pageSize={5} />);
        const prevPageButton = screen.getByText('<');
        expect(prevPageButton).toBeDisabled();
    });
});
