import React from "react";

import {
  Breadcrumb,
  BreadcrumbItem,
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
import { Components } from "./components/components";

export const ViewProduct: React.FC = () => {
  const contentRef1 = React.createRef<HTMLElement>();
  const contentRef2 = React.createRef<HTMLElement>();

  return (
    <>
      <PageSection type="breadcrumb">
        <Breadcrumb>
          <BreadcrumbItem to="#">Products</BreadcrumbItem>
          <BreadcrumbItem to="#" isActive>
            JBoss EAP
          </BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
      <PageSection variant={PageSectionVariants.light}>
        <TextContent>
          <Text component="h1">JBoss EAP</Text>
          <Text component="p">Product details</Text>
        </TextContent>
      </PageSection>
      <PageSection type="tabs">
        <Tabs defaultActiveKey={0} inset={{ default: "insetLg" }}>
          <Tab
            eventKey={0}
            title={<TabTitleText>Overview</TabTitleText>}
            tabContentRef={contentRef1}
          />
          <Tab
            eventKey={1}
            title={<TabTitleText>Components</TabTitleText>}
            tabContentRef={contentRef2}
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
          Overview
        </TabContent>
        <TabContent
          eventKey={2}
          id="refTab3Section"
          ref={contentRef2}
          aria-label="This is content for the third separate content tab"
          hidden
        >
          <Components />
        </TabContent>
      </PageSection>
    </>
  );
};
