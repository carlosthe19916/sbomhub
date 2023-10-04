import React from "react";

import {
  Button,
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
  PageSection,
  PageSectionVariants,
  Text,
  TextContent,
  Title,
  Toolbar,
  ToolbarContent,
  ToolbarItem,
} from "@patternfly/react-core";
import {
  getApiRequestParams,
  useLocalTableControls,
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
  name: string;
  severity: string;
  products: string[];
}

export const Vulnerabilities: React.FC = () => {
  const rows: RowData[] = [
    {
      name: "CVE-1",
      severity: "High",
      products: ["Product1", "Product3"],
    },
    {
      name: "CVE-2",
      severity: "Medium",
      products: ["Product7"],
    },
    {
      name: "CVE-3",
      severity: "Low",
      products: ["Product1"],
    },
  ];

  const tableControls = useLocalTableControls({
    idProperty: "name",
    items: rows,
    columnNames: {
      name: "Name",
      severity: "Severity",
      products: "Products",
    },
    hasActionsColumn: true,
    expandableVariant: "compound",
    filterCategories: [
      {
        key: "q",
        title: "Name",
        type: FilterType.search,
        placeholderText: "Filter by cve name...",
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
      getCompoundExpandTdProps,
    },
    expansionDerivedState: { isCellExpanded },
  } = tableControls;

  const [activeRowItem, setActiveRowItem] = React.useState<
    "vuln" | "product"
  >();

  return (
    <>
      <PageSection variant={PageSectionVariants.light}>
        <TextContent>
          <Text component="h1">Vulnerabilities</Text>
          <Text component="p">Search vulnerabilities</Text>
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
                  <Th {...getThProps({ columnKey: "severity" })} />
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
                  <Tbody key={item.name} isExpanded={isCellExpanded(item)}>
                    <Tr>
                      <TableRowContentWithControls
                        {...tableControls}
                        item={item}
                        rowIndex={rowIndex}
                      >
                        <Td
                          {...getCompoundExpandTdProps({
                            item: item,
                            rowIndex,
                            columnKey: "name",
                          })}
                        >
                          {item.name}
                        </Td>
                        <Td {...getTdProps({ columnKey: "severity" })}>
                          {item.severity}
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
                            {isCellExpanded(item, "name") && (
                              <div className="pf-v5-u-m-md">
                                <DescriptionList>
                                  <DescriptionListGroup>
                                    <DescriptionListTerm>
                                      Description
                                    </DescriptionListTerm>
                                    <DescriptionListDescription>
                                      keycloak: path traversal via double URL
                                      encoding. A flaw was found in Keycloak,
                                      where it does not properly validate URLs
                                      included in a redirect. An attacker can
                                      use this flaw to construct a malicious
                                      request to bypass validation and access
                                      other URLs and potentially sensitive
                                      information within the domain or possibly
                                      conduct further attacks. This flaw affects
                                      any client that utilizes a wildcard in the
                                      Valid Redirect URIs field.
                                    </DescriptionListDescription>
                                  </DescriptionListGroup>
                                  <DescriptionListGroup>
                                    <DescriptionListTerm>
                                      Reference
                                    </DescriptionListTerm>
                                    <DescriptionListDescription>
                                      <a href="https://access.redhat.com/security/cve/CVE-2022-3782">
                                        https://access.redhat.com/security/cve/CVE-2022-3782
                                      </a>
                                    </DescriptionListDescription>
                                  </DescriptionListGroup>
                                </DescriptionList>
                              </div>
                            )}
                            {isCellExpanded(item, "products") && (
                              <>
                                <List
                                  component={ListComponent.ol}
                                  type={OrderType.number}
                                >
                                  {item.products.map((e, vuln_index) => (
                                    <ListItem key={vuln_index}>
                                      <Button
                                        variant="link"
                                        onClick={() => {
                                          setActiveRowItem("product");
                                        }}
                                      >
                                        {e}
                                      </Button>
                                    </ListItem>
                                  ))}
                                </List>
                              </>
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
        </div>
      </PageSection>

      <DependencyAppsDetailDrawer
        entity={activeRowItem || null}
        onCloseClick={() => {
          setActiveRowItem(undefined);
        }}
      ></DependencyAppsDetailDrawer>
    </>
  );
};

export interface ICVEDetailDrawerProps
  extends Pick<IPageDrawerContentProps, "onCloseClick"> {
  entity: "vuln" | "product" | null;
}

export const DependencyAppsDetailDrawer: React.FC<ICVEDetailDrawerProps> = ({
  entity,
  onCloseClick,
}) => {
  const [isModalOpen, setIsModalOpen] = React.useState(false);

  return (
    <PageDrawerContent
      isExpanded={!!entity}
      onCloseClick={onCloseClick}
      focusKey={entity || undefined}
      pageKey="analysis-app-dependencies"
      drawerPanelContentProps={{ defaultSize: "600px" }}
    >
      {!entity ? (
        <EmptyState variant={EmptyStateVariant.sm}>
          <EmptyStateIcon icon={CubesIcon} />
          <Title headingLevel="h2" size="lg">
            No data
          </Title>
        </EmptyState>
      ) : (
        <>
          <Card isPlain>
            <CardTitle>Product1 affected by CVE-1</CardTitle>
            <CardBody>
              <DescriptionList>
                <DescriptionListGroup>
                  <DescriptionListTerm>Packages</DescriptionListTerm>
                  <DescriptionListDescription>
                    <List>
                      <ListItem>
                        <Button
                          variant="link"
                          onClick={() => setIsModalOpen(true)}
                        >
                          Package1
                        </Button>
                      </ListItem>
                      <ListItem>
                        <Button variant="link">Package2</Button>
                      </ListItem>
                      <ListItem>
                        <Button variant="link">Package3</Button>
                      </ListItem>
                    </List>
                  </DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>CVE-1 description</DescriptionListTerm>
                  <DescriptionListDescription>
                    keycloak: path traversal via double URL encoding. A flaw was
                    found in Keycloak, where it does not properly validate URLs
                    included in a redirect. An attacker can use this flaw to
                    construct a malicious request to bypass validation and
                    access other URLs and potentially sensitive information
                    within the domain or possibly conduct further attacks. This
                    flaw affects any client that utilizes a wildcard in the
                    Valid Redirect URIs field.
                  </DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>CVE-1 proposed fix</DescriptionListTerm>
                  <DescriptionListDescription>
                    Upgrade the dependency to the latest version 8.0 E.g.
                    org.keycloak:testsuite:8.0
                  </DescriptionListDescription>
                </DescriptionListGroup>
              </DescriptionList>
            </CardBody>
          </Card>
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
