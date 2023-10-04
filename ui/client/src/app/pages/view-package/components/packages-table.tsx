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
  Card,
  CardBody,
  CardTitle,
  DescriptionList,
  DescriptionListDescription,
  DescriptionListGroup,
  DescriptionListTerm,
  EmptyState,
  EmptyStateIcon,
  EmptyStateVariant,
  Flex,
  FlexItem,
  List,
  ListComponent,
  ListItem,
  Modal,
  OrderType,
  Tab,
  TabTitleText,
  Tabs,
  Text,
  TextContent,
  Title,
  Toolbar,
  ToolbarContent,
  ToolbarGroup,
  ToolbarItem,
} from "@patternfly/react-core";
import {
  ActionsColumn,
  ExpandableRowContent,
  Table,
  Tbody,
  Td,
  Th,
  Thead,
  Tr,
} from "@patternfly/react-table";
import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import ShieldIcon from "@patternfly/react-icons/dist/esm/icons/shield-alt-icon";
import {
  global_info_color_100 as lowColor,
  global_warning_color_100 as moderateColor,
  global_danger_color_100 as importantColor,
  global_palette_purple_400 as criticalColor,
} from "@patternfly/react-tokens";
import {
  IPageDrawerContentProps,
  PageDrawerContent,
} from "@app/shared/components/PageDrawerContext";
import CubesIcon from "@patternfly/react-icons/dist/esm/icons/cubes-icon";
import { CodeEditor, Language } from "@patternfly/react-code-editor";

interface RowData {
  version: string;
  vulnerabilities: string[];
  products: string[];
}

export const PackagesTable: React.FC = () => {
  const rows: RowData[] = [
    {
      version: "1.2.17",
      vulnerabilities: ["CVE-1", "CVE-2"],
      products: ["JBoss EAP", "Quarkus"],
    },
    {
      version: "1.2.16",
      vulnerabilities: ["CVE-1", "CVE-2"],
      products: ["Product1"],
    },
    {
      version: "1.2.15",
      vulnerabilities: ["CVE-1", "CVE-2"],
      products: ["Product1"],
    },
    {
      version: "1.2.14",
      vulnerabilities: ["CVE-1", "CVE-2"],
      products: ["Product1"],
    },
    {
      version: "1.2.13",
      vulnerabilities: ["CVE-1", "CVE-2"],
      products: ["Product1"],
    },
  ];

  const tableControls = useLocalTableControls({
    idProperty: "version",
    items: rows,
    columnNames: {
      version: "Version",
      vulnerabilities: "Vulnerabilities",
      products: "Products",
    },
    hasActionsColumn: true,
    expandableVariant: "compound",
    filterCategories: [
      {
        key: "q",
        title: "Version",
        type: FilterType.search,
        placeholderText: "Filter by version...",
      },
    ],
    sortableColumns: ["version"],
    getSortValues: (item) => ({
      version: item?.version || "",
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
      getCompoundExpandTdProps,
    },
    expansionDerivedState: { isCellExpanded },
  } = tableControls;

  const [activeRowItem, setActiveRowItem] = React.useState<
    "vulnerability" | "product"
  >();

  return (
    <>
      <div
        style={{
          backgroundColor: "var(--pf-v5-global--BackgroundColor--100)",
        }}
      >
        <Toolbar {...toolbarProps}>
          <ToolbarContent>
            <FilterToolbar {...filterToolbarProps} showFiltersSideBySide />
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
                <Th {...getThProps({ columnKey: "version" })} />
                <Th {...getThProps({ columnKey: "vulnerabilities" })} />
                <Th {...getThProps({ columnKey: "products" })} />
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
                <Tbody key={item.version} isExpanded={isCellExpanded(item)}>
                  <Tr>
                    <TableRowContentWithControls
                      {...tableControls}
                      item={item}
                      rowIndex={rowIndex}
                    >
                      <Td>{item.version}</Td>
                      <Td
                        {...getCompoundExpandTdProps({
                          item: item,
                          rowIndex,
                          columnKey: "vulnerabilities",
                        })}
                      >
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
                              <FlexItem>0</FlexItem>
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
                              <FlexItem>1</FlexItem>
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
                              <FlexItem>9</FlexItem>
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
                              <FlexItem>6</FlexItem>
                            </Flex>
                          </FlexItem>
                        </Flex>
                      </Td>
                      <Td
                        {...getCompoundExpandTdProps({
                          item: item,
                          rowIndex,
                          columnKey: "products",
                        })}
                      >
                        {item.products.length}
                      </Td>
                    </TableRowContentWithControls>
                  </Tr>
                  {isCellExpanded(item) ? (
                    <Tr isExpanded>
                      <Td
                        {...getExpandedContentTdProps({
                          item: item,
                        })}
                      >
                        <ExpandableRowContent>
                          {isCellExpanded(item, "vulnerabilities") && (
                            <div className="pf-v5-u-m-md">
                              <List
                                component={ListComponent.ol}
                                type={OrderType.number}
                              >
                                {item.vulnerabilities.map((e, vuln_index) => (
                                  <ListItem key={vuln_index}>
                                    <Button
                                      variant="link"
                                      onClick={() =>
                                        setActiveRowItem("vulnerability")
                                      }
                                    >
                                      {e}
                                    </Button>
                                  </ListItem>
                                ))}
                              </List>
                            </div>
                          )}
                          {isCellExpanded(item, "products") && (
                            <div className="pf-v5-u-m-md">
                              <List
                                component={ListComponent.ol}
                                type={OrderType.number}
                              >
                                {item.products.map((e, vuln_index) => (
                                  <ListItem key={vuln_index}>
                                    <Button
                                      variant="link"
                                      onClick={() =>
                                        setActiveRowItem("product")
                                      }
                                    >
                                      {e}
                                    </Button>
                                  </ListItem>
                                ))}
                              </List>
                            </div>
                          )}
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

        <DependencyAppsDetailDrawer
          cve={activeRowItem || null}
          onCloseClick={() => setActiveRowItem(undefined)}
        ></DependencyAppsDetailDrawer>
      </div>
    </>
  );
};

export interface ICVEDetailDrawerProps
  extends Pick<IPageDrawerContentProps, "onCloseClick"> {
  cve: "vulnerability" | "product" | null;
}

export const DependencyAppsDetailDrawer: React.FC<ICVEDetailDrawerProps> = ({
  cve,
  onCloseClick,
}) => {
  const [isModalOpen, setIsModalOpen] = React.useState(false);

  return (
    <PageDrawerContent
      isExpanded={!!cve}
      onCloseClick={onCloseClick}
      focusKey={cve || undefined}
      pageKey="analysis-app-dependencies"
      drawerPanelContentProps={{ defaultSize: "600px" }}
    >
      {!cve ? (
        <EmptyState variant={EmptyStateVariant.sm}>
          <EmptyStateIcon icon={CubesIcon} />
          <Title headingLevel="h2" size="lg">
            No data
          </Title>
        </EmptyState>
      ) : (
        <>
          {cve === "vulnerability" && (
            <Card isPlain>
              <CardTitle>Vulnerability: CVE-1</CardTitle>
              <CardBody>
                <DescriptionList>
                  <DescriptionListGroup>
                    <DescriptionListTerm>CVE-1 description</DescriptionListTerm>
                    <DescriptionListDescription>
                      keycloak: path traversal via double URL encoding. A flaw
                      was found in Keycloak, where it does not properly validate
                      URLs included in a redirect. An attacker can use this flaw
                      to construct a malicious request to bypass validation and
                      access other URLs and potentially sensitive information
                      within the domain or possibly conduct further attacks.
                      This flaw affects any client that utilizes a wildcard in
                      the Valid Redirect URIs field.
                    </DescriptionListDescription>
                  </DescriptionListGroup>
                  <DescriptionListGroup>
                    <DescriptionListTerm>
                      CVE-1 proposed fix
                    </DescriptionListTerm>
                    <DescriptionListDescription>
                      Upgrade the dependency to the latest version 8.0 E.g.
                      org.keycloak:testsuite:8.0
                    </DescriptionListDescription>
                  </DescriptionListGroup>
                </DescriptionList>
              </CardBody>
            </Card>
          )}
          {cve === "product" && (
            <Card isPlain>
              <CardTitle>Product: JBoss EAP</CardTitle>
              <CardBody>
                <DescriptionList>
                  <DescriptionListGroup>
                    <DescriptionListTerm>
                      Package that originates vulnerabilities
                    </DescriptionListTerm>
                    <DescriptionListDescription>
                      <Button
                        variant="link"
                        onClick={() => setIsModalOpen(true)}
                      >
                        Log4j:1.2.17
                      </Button>
                    </DescriptionListDescription>
                  </DescriptionListGroup>
                  <DescriptionListGroup>
                    <DescriptionListTerm>CVEs</DescriptionListTerm>
                    <DescriptionListDescription>
                      <List>
                        <ListItem>CVE-1</ListItem>
                        <ListItem>CVE-2</ListItem>
                      </List>
                    </DescriptionListDescription>
                  </DescriptionListGroup>
                </DescriptionList>
              </CardBody>
            </Card>
          )}
        </>
      )}

      <Modal
        title="Package detals"
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        actions={[
          <Button
            key="cancel"
            variant="link"
            onClick={() => setIsModalOpen(false)}
          >
            Close
          </Button>,
        ]}
        ouiaId="BasicModal"
      >
        <CodeEditor
          isDarkTheme
          isLineNumbersVisible
          isReadOnly
          isMinimapVisible
          isLanguageLabelVisible
          code={treeContent}
          language={Language.plaintext}
          height="400px"
        />
      </Modal>
    </PageDrawerContent>
  );
};

const treeContent = `
/etc
├── abrt
│   ├── abrt-action-save-package-data.conf
│   ├── abrt.conf
│   ├── gpg_keys.conf
│   └── plugins
│       ├── CCpp.conf
│       ├── java.conf
│       ├── oops.conf
│       ├── python3.conf
│       ├── vmcore.conf
│       └── xorg.conf
├── adjtime
├── aliases
├── alsa
│   ├── alsactl.conf
│   ├── conf.d
│   │   ├── 50-pipewire.conf
│   │   └── 99-pipewire-default.conf
│   └── state-daemon.conf
├── alternatives
│   ├── alt-java -> /usr/lib/jvm/java-17-openjdk-17.0.8.0.7-1.fc38.x86_64/bin/alt-java
│   ├── alt-java.1.gz -> /usr/share/man/man1/alt-java-java-17-openjdk-17.0.8.0.7-1.fc38.x86_64.1.gz
│   ├── apropos -> /usr/bin/apropos.man-db
│   ├── apropos.1.gz -> /usr/share/man/man1/apropos.man-db.1.gz
│   ├── arptables -> /usr/sbin/arptables-nft
│   ├── arptables-helper -> /usr/libexec/arptables-nft-helper
│   ├── arptables-man -> /usr/share/man/man8/arptables-nft.8.gz
│   ├── arptables-restore -> /usr/sbin/arptables-nft-restore
│   ├── arptables-restore-man -> /usr/share/man/man8/arptables-nft-restore.8.gz
│   ├── arptables-save -> /usr/sbin/arptables-nft-save
│   ├── arptables-save-man -> /usr/share/man/man8/arptables-nft-save.8.gz
│   ├── cdrecord -> /usr/bin/xorrecord
│   ├── cdrecord-cdrecordman -> /usr/share/man/man1/xorrecord.1.gz
│   ├── cifs-idmap-plugin -> /usr/lib64/cifs-utils/cifs_idmap_sss.so
│   ├── cups_backend_smb -> /usr/bin/smbspool
│   ├── ebtables -> /usr/sbin/ebtables-nft
│   ├── ebtables-man -> /usr/share/man/man8/ebtables-nft.8.gz
│   ├── ebtables-restore -> /usr/sbin/ebtables-nft-restore
│   ├── ebtables-save -> /usr/sbin/ebtables-nft-save
│   ├── go -> /usr/lib/golang/bin/go
│   ├── gofmt -> /usr/lib/golang/bin/gofmt
│   ├── google-chrome -> /usr/bin/google-chrome-stable
│   ├── ip6tables -> /usr/sbin/ip6tables-nft
`;
