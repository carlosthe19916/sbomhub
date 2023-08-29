import React from "react";

import {
  Button,
  PageSection,
  PageSectionVariants,
  Text,
  TextContent,
  Toolbar,
  ToolbarContent,
  ToolbarItem,
} from "@patternfly/react-core";
import {
  getApiRequestParams,
  useTableControlProps,
  useTableControlUrlParams,
} from "@app/shared/hooks/table-controls";
import {
  FilterToolbar,
  FilterType,
} from "@app/shared/components/FilterToolbar";
import { useFetchPackages } from "@app/queries/packages";
import { useSelectionState } from "@app/shared/hooks/useSelectionState";
import { SimplePagination } from "@app/shared/components/SimplePagination";
import {
  ExpandableRowContent,
  Table,
  Tbody,
  Td,
  Th,
  Thead,
  Tr,
} from "@patternfly/react-table";
import {
  ConditionalTableBody,
  TableHeaderContentWithControls,
  TableRowContentWithControls,
} from "@app/shared/components/TableControls";
import DownloadIcon from "@patternfly/react-icons/dist/esm/icons/download-icon";
import spacing from "@patternfly/react-styles/css/utilities/Spacing/spacing";
import { Link, NavLink } from "react-router-dom";

export const Packages: React.FC = () => {
  const tableControlState = useTableControlUrlParams({
    columnNames: {
      name: "Name",
      version: "Version",
      supplier: "Supplier",
      created: "Created",
      dependencies: "Dependencies",
      advisories: "Advisories",
      download: "Download",
    },
    sortableColumns: [],
    initialSort: null,
    filterCategories: [
      {
        key: "q",
        title: "Name",
        type: FilterType.search,
        placeholderText: "Search",
      },
    ],
    initialItemsPerPage: 10,
    expandableVariant: "single",
  });

  const {
    result: { data: currentPageItems, total: totalItemCount },
    isFetching,
    fetchError,
  } = useFetchPackages(
    getApiRequestParams({
      ...tableControlState, // Includes filterState, sortState and paginationState
    })
  );

  const tableControls = useTableControlProps({
    ...tableControlState, // Includes filterState, sortState and paginationState
    idProperty: "id",
    currentPageItems,
    totalItemCount,
    isLoading: isFetching,
    selectionState: useSelectionState({
      items: currentPageItems,
      isEqual: (a, b) => a.id === b.id,
    }),
  });

  const {
    numRenderedColumns,
    propHelpers: {
      toolbarProps,
      filterToolbarProps,
      paginationToolbarItemProps,
      paginationProps,
      tableProps,
      getThProps,
      getTdProps,
      getExpandedContentTdProps,
    },
    expansionDerivedState: { isCellExpanded },
  } = tableControls;

  return (
    <>
      <PageSection variant={PageSectionVariants.light}>
        <TextContent>
          <Text component="h1">Packages</Text>
          <Text component="p">Search packages</Text>
        </TextContent>
      </PageSection>
      <PageSection>
        <div
          style={{
            backgroundColor: "var(--pf-v5-global--BackgroundColor--100)",
          }}
        >
          <Toolbar {...toolbarProps}>
            <ToolbarContent>
              <FilterToolbar {...filterToolbarProps} />
              <ToolbarItem {...paginationToolbarItemProps}>
                <SimplePagination
                  idPrefix="packages-table"
                  isTop
                  paginationProps={paginationProps}
                />
              </ToolbarItem>
            </ToolbarContent>
          </Toolbar>

          <Table {...tableProps} aria-label="Packages table">
            <Thead>
              <Tr>
                <TableHeaderContentWithControls {...tableControls}>
                  <Th {...getThProps({ columnKey: "name" })} />
                  <Th {...getThProps({ columnKey: "version" })} />
                  <Th {...getThProps({ columnKey: "supplier" })} />
                  <Th {...getThProps({ columnKey: "created" })} />
                  <Th {...getThProps({ columnKey: "dependencies" })} />
                  <Th {...getThProps({ columnKey: "advisories" })} />
                  <Th {...getThProps({ columnKey: "download" })} />
                </TableHeaderContentWithControls>
              </Tr>
            </Thead>
            <ConditionalTableBody
              isLoading={isFetching}
              isError={!!fetchError}
              isNoData={totalItemCount === 0}
              numRenderedColumns={numRenderedColumns}
            >
              {currentPageItems?.map((item, rowIndex) => {
                return (
                  <Tbody key={item.id} isExpanded={isCellExpanded(item)}>
                    <Tr>
                      <TableRowContentWithControls
                        {...tableControls}
                        item={item}
                        rowIndex={rowIndex}
                      >
                        <Td width={15} {...getTdProps({ columnKey: "name" })}>
                          <NavLink to={`/package/${item.id}`}>
                            {item.name}
                          </NavLink>
                        </Td>
                        <Td
                          width={20}
                          modifier="truncate"
                          {...getTdProps({ columnKey: "version" })}
                        >
                          {item.version}
                        </Td>
                        <Td
                          width={20}
                          {...getTdProps({ columnKey: "supplier" })}
                        >
                          {item.supplier}
                        </Td>
                        <Td {...getTdProps({ columnKey: "created" })}>
                          {item.created}
                        </Td>
                        <Td {...getTdProps({ columnKey: "dependencies" })}>
                          {item.dependencies.length}
                        </Td>
                        <Td {...getTdProps({ columnKey: "advisories" })}>
                          {item.advisories.length}
                        </Td>
                        <Td {...getTdProps({ columnKey: "download" })}>
                          <Button
                            variant="plain"
                            icon={<DownloadIcon />}
                            component="a"
                            href={item.href}
                            target="_blank"
                          />
                        </Td>
                      </TableRowContentWithControls>
                    </Tr>
                    {isCellExpanded(item) ? (
                      <Tr isExpanded>
                        <Td />
                        <Td
                          {...getExpandedContentTdProps({ item })}
                          className={spacing.pyLg}
                        >
                          <ExpandableRowContent>
                            No description available
                          </ExpandableRowContent>
                        </Td>
                      </Tr>
                    ) : null}
                  </Tbody>
                );
              })}
            </ConditionalTableBody>
          </Table>

          <SimplePagination
            idPrefix="dependencies-table"
            isTop={false}
            paginationProps={paginationProps}
          />
        </div>
      </PageSection>
    </>
  );
};
