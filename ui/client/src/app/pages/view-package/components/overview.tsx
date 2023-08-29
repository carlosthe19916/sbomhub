import { PackageDetails } from "@app/api/models";
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
  List,
  ListItem,
} from "@patternfly/react-core";
import React from "react";

interface OverviewProps {
  packageDetails: PackageDetails;
}

export const Overview: React.FC<OverviewProps> = ({ packageDetails }) => {
  return (
    <>
      <Grid hasGutter>
        <GridItem md={3}>
          <Card isFullHeight>
            <CardTitle>Metadata</CardTitle>
            <CardBody>
              <DescriptionList>
                <DescriptionListGroup>
                  <DescriptionListTerm>Name</DescriptionListTerm>
                  <DescriptionListDescription>
                    {packageDetails.name}
                  </DescriptionListDescription>
                </DescriptionListGroup>
                {/* <DescriptionListGroup>
                  <DescriptionListTerm>ID</DescriptionListTerm>
                  <DescriptionListDescription>
                    {packageDetails.SPDXID}
                  </DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>Namespace</DescriptionListTerm>
                  <DescriptionListDescription>
                    {packageDetails.documentNamespace}
                  </DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>SPDX Version</DescriptionListTerm>
                  <DescriptionListDescription>
                    {packageDetails.spdxVersion}
                  </DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>Data License</DescriptionListTerm>
                  <DescriptionListDescription>
                    {packageDetails.dataLicense}
                  </DescriptionListDescription>
                </DescriptionListGroup> */}
              </DescriptionList>
            </CardBody>
          </Card>
        </GridItem>
        {/* <GridItem md={3}>
          <Card isFullHeight>
            <CardTitle>Creation</CardTitle>
            <CardBody>
              <DescriptionList>
                <DescriptionListGroup>
                  <DescriptionListTerm>Created</DescriptionListTerm>
                  <DescriptionListDescription>
                    {packageDetails.creationInfo.created}
                  </DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>
                    License List Version
                  </DescriptionListTerm>
                  <DescriptionListDescription>
                    {packageDetails.creationInfo.licenseListVersion}
                  </DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>Creator</DescriptionListTerm>
                  <DescriptionListDescription>
                    <List>
                      {packageDetails.creationInfo.creators.map((item) => (
                        <ListItem>{item}</ListItem>
                      ))}
                    </List>
                  </DescriptionListDescription>
                </DescriptionListGroup>
              </DescriptionList>
            </CardBody>
          </Card>
        </GridItem>
        <GridItem md={3}>
          <Card isFullHeight>
            <CardTitle>Creation</CardTitle>
            <CardBody>
              <DescriptionList>
                <DescriptionListGroup>
                  <DescriptionListTerm>Statistics</DescriptionListTerm>
                  <DescriptionListDescription>
                    {packageDetails.creationInfo.created}
                  </DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>Size</DescriptionListTerm>
                  <DescriptionListDescription>_</DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>Packages</DescriptionListTerm>
                  <DescriptionListDescription>
                    {packageDetails.packages.length}
                  </DescriptionListDescription>
                </DescriptionListGroup>
              </DescriptionList>
            </CardBody>
          </Card>
        </GridItem>
        <GridItem md={3}>
          <Card isFullHeight>
            <CardTitle>Package</CardTitle>
            <CardBody>
              <DescriptionList>
                <DescriptionListGroup>
                  <DescriptionListTerm>Name</DescriptionListTerm>
                  <DescriptionListDescription>_</DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>Version</DescriptionListTerm>
                  <DescriptionListDescription>_</DescriptionListDescription>
                </DescriptionListGroup>
                <DescriptionListGroup>
                  <DescriptionListTerm>External References</DescriptionListTerm>
                  <DescriptionListDescription>_</DescriptionListDescription>
                </DescriptionListGroup>
              </DescriptionList>
            </CardBody>
          </Card>
        </GridItem> */}
      </Grid>
    </>
  );
};
