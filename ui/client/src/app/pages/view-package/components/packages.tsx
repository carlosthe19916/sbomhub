import { PackageDetails } from "@app/api/models";
import {
  FilterToolbar,
  FilterType,
} from "@app/shared/components/FilterToolbar";
import { SimplePagination } from "@app/shared/components/SimplePagination";
import {
  ConditionalTableBody,
  TableHeaderContentWithControls,
  TableRowContentWithControls,
} from "@app/shared/components/TableControls";
import { useLocalTableControls } from "@app/shared/hooks/table-controls";
import {
  Card,
  CardBody,
  CardTitle,
  DescriptionList,
  DescriptionListDescription,
  DescriptionListGroup,
  DescriptionListTerm,
  Grid,
  GridItem,
  Toolbar,
  ToolbarContent,
  ToolbarGroup,
  ToolbarItem,
} from "@patternfly/react-core";
import {
  ExpandableRowContent,
  Table,
  Tbody,
  Td,
  Th,
  Thead,
  Tr,
} from "@patternfly/react-table";
import React from "react";
import spacing from "@patternfly/react-styles/css/utilities/Spacing/spacing";

interface PackagesProps {
  packageDetails: PackageDetails;
}

export const Packages: React.FC<PackagesProps> = ({ packageDetails }) => {
  const tableControls = useLocalTableControls({
    idProperty: "name",
    items: packageDetails.packages,
    columnNames: {
      name: "Name",
      version: "Version",
    },
    expandableVariant: "single",
    hasActionsColumn: true,
    filterCategories: [
      {
        key: "q",
        title: "Name",
        type: FilterType.search,
        placeholderText: "Search",
      },
    ],
    sortableColumns: ["name"],
    getSortValues: (item) => ({
      name: item?.name || "",
    }),
    hasPagination: true,
  });

  const {
    currentPageItems,
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
                idPrefix="stakeholders-table"
                isTop
                paginationProps={paginationProps}
              />
            </ToolbarItem>
          </ToolbarContent>
        </Toolbar>
        <Table {...tableProps} isExpandable aria-label="Packages table">
          <Thead>
            <Tr>
              <TableHeaderContentWithControls {...tableControls}>
                <Th {...getThProps({ columnKey: "name" })} />
                <Th {...getThProps({ columnKey: "version" })} />
              </TableHeaderContentWithControls>
            </Tr>
          </Thead>
          <ConditionalTableBody numRenderedColumns={numRenderedColumns}>
            {currentPageItems?.map((pkg, rowIndex) => {
              return (
                <Tbody key={pkg.name} isExpanded={isCellExpanded(pkg)}>
                  <Tr>
                    <TableRowContentWithControls
                      {...tableControls}
                      item={pkg}
                      rowIndex={rowIndex}
                    >
                      <Td {...getTdProps({ columnKey: "name" })}>{pkg.name}</Td>
                      <Td {...getTdProps({ columnKey: "version" })}>
                        {pkg.versionInfo}
                      </Td>
                    </TableRowContentWithControls>
                  </Tr>
                  {isCellExpanded(pkg) ? (
                    <Tr isExpanded>
                      <Td />
                      <Td
                        {...getExpandedContentTdProps({ item: pkg })}
                        className={spacing.pyLg}
                      >
                        <ExpandableRowContent>
                          <Grid hasGutter>
                            <GridItem md={4}>
                              <Card isPlain>
                                <CardTitle>Information</CardTitle>
                                <CardBody>
                                  <DescriptionList>
                                    <DescriptionListGroup>
                                      <DescriptionListTerm>
                                        Download
                                      </DescriptionListTerm>
                                      <DescriptionListDescription>
                                        _
                                      </DescriptionListDescription>
                                    </DescriptionListGroup>
                                    <DescriptionListGroup>
                                      <DescriptionListTerm>
                                        Copyright
                                      </DescriptionListTerm>
                                      <DescriptionListDescription>
                                        _
                                      </DescriptionListDescription>
                                    </DescriptionListGroup>
                                    <DescriptionListGroup>
                                      <DescriptionListTerm>
                                        License (declared)
                                      </DescriptionListTerm>
                                      <DescriptionListDescription>
                                        _
                                      </DescriptionListDescription>
                                    </DescriptionListGroup>
                                    <DescriptionListGroup>
                                      <DescriptionListTerm>
                                        License (concluded)
                                      </DescriptionListTerm>
                                      <DescriptionListDescription>
                                        _
                                      </DescriptionListDescription>
                                    </DescriptionListGroup>
                                  </DescriptionList>
                                </CardBody>
                              </Card>
                            </GridItem>
                            <GridItem md={4}>
                              <Card isPlain>
                                <CardTitle>External references</CardTitle>
                                <CardBody>_</CardBody>
                              </Card>
                            </GridItem>
                            <GridItem md={4}>
                              <Card isPlain>
                                <CardTitle>Relationships</CardTitle>
                                <CardBody>_</CardBody>
                              </Card>
                            </GridItem>
                          </Grid>
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
          idPrefix="packages-table"
          isTop={false}
          paginationProps={paginationProps}
        />
      </div>
    </>
  );
};
