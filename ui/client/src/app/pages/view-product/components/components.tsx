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
  Button,
  ButtonVariant,
  Flex,
  FlexItem,
  Label,
  Tab,
  TabTitleText,
  Tabs,
  Toolbar,
  ToolbarContent,
  ToolbarGroup,
  ToolbarItem,
} from "@patternfly/react-core";
import {
  ActionsColumn,
  Table,
  Tbody,
  Td,
  Th,
  Thead,
  Tr,
} from "@patternfly/react-table";
import React from "react";
import { NavLink } from "react-router-dom";
import ShieldIcon from "@patternfly/react-icons/dist/esm/icons/shield-alt-icon";
import {
  global_info_color_100 as lowColor,
  global_warning_color_100 as moderateColor,
  global_danger_color_100 as importantColor,
  global_palette_purple_400 as criticalColor,
} from "@patternfly/react-tokens";

export const Components: React.FC = () => {
  const tabComponentRef = React.useRef<any>();

  const [activeTabKey, setActiveTabKey] = React.useState<number>(0);
  const [tabs, setTabs] = React.useState<string[]>([
    "JBoss EAP",
    "JBoss EAP XP",
    "JBoss EAP Openshift",
  ]);

  const [isModalOpen, setIsModalOpen] = React.useState(false);

  return (
    <Tabs
      isBox
      activeKey={activeTabKey}
      onSelect={(event: any, tabIndex: string | number) =>
        setActiveTabKey(tabIndex as number)
      }
      onAdd={() => setIsModalOpen(true)}
      ref={tabComponentRef}
    >
      {tabs.map((tab, index) => (
        <Tab
          key={index}
          eventKey={index}
          title={<TabTitleText>{tab}</TabTitleText>}
        >
          {/* <div
            style={{
              backgroundColor: "var(--pf-v5-global--BackgroundColor--100)",
            }}
          >
            <Tabs isSecondary defaultActiveKey={1} variant="default">
              <Tab eventKey={1} title={<TabTitleText>SBOMs</TabTitleText>}> */}
          <SBOMTable />
          {/* </Tab>
              <Tab eventKey={2} title={<TabTitleText>Settings</TabTitleText>}>
                Secondary tab item 2 section
              </Tab>
            </Tabs>
          </div> */}
        </Tab>
      ))}
    </Tabs>
  );
};

interface RowData {
  name: string;
  format: string;
  productVersion: string;
}

export const SBOMTable: React.FC = () => {
  const rows: RowData[] = [
    {
      name: "JBoss EAP 6.3",
      format: "CycloneDX",
      productVersion: "6.3",
    },
    {
      name: "JBoss EAP 7.4",
      format: "CycloneDX",
      productVersion: "7.4",
    },
    {
      name: "JBoss EAP 8.0 Beta",
      format: "CycloneDX",
      productVersion: "8.0-Beta",
    },
  ];

  const tableControls = useLocalTableControls({
    idProperty: "name",
    items: rows,
    columnNames: {
      name: "Name",
      format: "Format",
      vulnerabilities: "Vulnerabilities",
      productVersion: "Product version",
    },
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
    },
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
            {/* <ToolbarGroup variant="button-group">
              <ToolbarItem>
                <Button
                  type="button"
                  id="create-product"
                  aria-label="Create new product"
                  variant={ButtonVariant.primary}
                  onClick={() => setCreateUpdateModalState("create")}
                >
                  Create new
                </Button>
              </ToolbarItem>
            </ToolbarGroup> */}
            <ToolbarItem {...paginationToolbarItemProps}>
              <SimplePagination
                idPrefix="products-table"
                isTop
                paginationProps={paginationProps}
              />
            </ToolbarItem>
          </ToolbarContent>
        </Toolbar>

        <Table {...tableProps} aria-label="Products table">
          <Thead>
            <Tr>
              <TableHeaderContentWithControls {...tableControls}>
                <Th {...getThProps({ columnKey: "name" })} />
                <Th {...getThProps({ columnKey: "format" })} />
                <Th {...getThProps({ columnKey: "vulnerabilities" })} />
                <Th {...getThProps({ columnKey: "productVersion" })} />
              </TableHeaderContentWithControls>
            </Tr>
          </Thead>
          <ConditionalTableBody
            isLoading={false}
            // isError={!!fetchError}
            isNoData={rows.length === 0}
            numRenderedColumns={numRenderedColumns}
          >
            {currentPageItems?.map((item, rowIndex) => {
              return (
                <Tbody key={item.name}>
                  <Tr>
                    <TableRowContentWithControls
                      {...tableControls}
                      item={item}
                      rowIndex={rowIndex}
                    >
                      <Td {...getTdProps({ columnKey: "name" })}>
                        <NavLink to={`/products/test/sboms/1`}>
                          {item.name}
                        </NavLink>
                      </Td>
                      <Td {...getTdProps({ columnKey: "format" })}>
                        {item.format}
                      </Td>
                      <Td {...getTdProps({ columnKey: "vulnerabilities" })}>
                        <Flex
                          spaceItems={{ default: "spaceItemsSm" }}
                          alignItems={{ default: "alignItemsCenter" }}
                          flexWrap={{ default: "nowrap" }}
                          style={{ whiteSpace: "nowrap" }}
                        >
                          <FlexItem>
                            <Flex
                              spaceItems={{ default: "spaceItemsSm" }}
                              alignItems={{ default: "alignItemsCenter" }}
                              flexWrap={{ default: "nowrap" }}
                              style={{ whiteSpace: "nowrap" }}
                            >
                              <FlexItem>
                                <ShieldIcon color={lowColor.value} />
                              </FlexItem>
                              <FlexItem>709</FlexItem>
                            </Flex>
                          </FlexItem>
                          <FlexItem>
                            <Flex
                              spaceItems={{ default: "spaceItemsSm" }}
                              alignItems={{ default: "alignItemsCenter" }}
                              flexWrap={{ default: "nowrap" }}
                              style={{ whiteSpace: "nowrap" }}
                            >
                              <FlexItem>
                                <ShieldIcon color={moderateColor.value} />
                              </FlexItem>
                              <FlexItem>20</FlexItem>
                            </Flex>
                          </FlexItem>
                          <FlexItem>
                            <Flex
                              spaceItems={{ default: "spaceItemsSm" }}
                              alignItems={{ default: "alignItemsCenter" }}
                              flexWrap={{ default: "nowrap" }}
                              style={{ whiteSpace: "nowrap" }}
                            >
                              <FlexItem>
                                <ShieldIcon color={importantColor.value} />
                              </FlexItem>
                              <FlexItem>590</FlexItem>
                            </Flex>
                          </FlexItem>
                          <FlexItem>
                            <Flex
                              spaceItems={{ default: "spaceItemsSm" }}
                              alignItems={{ default: "alignItemsCenter" }}
                              flexWrap={{ default: "nowrap" }}
                              style={{ whiteSpace: "nowrap" }}
                            >
                              <FlexItem>
                                <ShieldIcon color={criticalColor.value} />
                              </FlexItem>
                              <FlexItem>56</FlexItem>
                            </Flex>
                          </FlexItem>
                        </Flex>
                      </Td>
                      <Td {...getTdProps({ columnKey: "productVersion" })}>
                        <Label color="blue">{item.productVersion}</Label>
                      </Td>
                      <Td isActionCell>
                        <ActionsColumn
                          items={[
                            {
                              title: "Download",
                              onClick: () => {},
                            },
                            {
                              title: "Delete",
                              onClick: () => {},
                            },
                          ]}
                        />
                      </Td>
                    </TableRowContentWithControls>
                  </Tr>
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
    </>
  );
};
