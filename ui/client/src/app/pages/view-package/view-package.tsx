import React from "react";

import {
  PageSection,
  PageSectionVariants,
  Spinner,
  Tab,
  TabContent,
  TabTitleText,
  Tabs,
  Text,
  TextContent,
} from "@patternfly/react-core";
import { usePackageById } from "@app/queries/packages";
import { useParams } from "react-router-dom";
import { Overview } from "./components/overview";
import { Packages } from "./components/packages";
import { Source } from "./components/source";

export const ViewPackage: React.FC = () => {
  const { packageId } = useParams();

  const { result, isFetching } = usePackageById(packageId || "");

  const contentRef1 = React.createRef<HTMLElement>();
  const contentRef2 = React.createRef<HTMLElement>();
  const contentRef3 = React.createRef<HTMLElement>();

  return (
    <>
      <PageSection variant={PageSectionVariants.light}>
        <TextContent>
          <Text component="h1">{packageId}</Text>
          <Text component="p">SBOM detail information</Text>
        </TextContent>
      </PageSection>
      <PageSection type="tabs">
        <Tabs defaultActiveKey={0} inset={{ default: "insetLg" }}>
          <Tab
            eventKey={0}
            title={<TabTitleText>Overview</TabTitleText>}
            tabContentRef={contentRef1}
          />
          {/* <Tab
            eventKey={1}
            title={<TabTitleText>Packages</TabTitleText>}
            tabContentRef={contentRef2}
          /> */}
          <Tab
            eventKey={2}
            title={<TabTitleText>Source</TabTitleText>}
            tabContentRef={contentRef3}
          />
        </Tabs>
      </PageSection>
      <PageSection>
        <TabContent
          eventKey={0}
          id="refTab1Section"
          ref={contentRef1}
          aria-label="This is content for the first separate content tab"
        >
          {result && <Overview packageDetails={result} />}
        </TabContent>
        {/* <TabContent
          eventKey={1}
          id="refTab2Section"
          ref={contentRef2}
          aria-label="This is content for the second separate content tab"
          hidden
        >
          {result && <Packages packageDetails={result} />}
        </TabContent> */}
        <TabContent
          eventKey={2}
          id="refTab3Section"
          ref={contentRef3}
          aria-label="This is content for the third separate content tab"
          hidden
        >
          {result && <Source packageDetails={result} />}
        </TabContent>
      </PageSection>
    </>
  );
};
