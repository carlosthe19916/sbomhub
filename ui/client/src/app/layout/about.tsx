import React from "react";

import {
  AboutModal,
  TextContent,
  TextList,
  TextListItem,
} from "@patternfly/react-core";

import logo from "../images/logo.svg";
import backgroundImage from "../images/pfbg-icon.svg";

interface IButtonAboutAppProps {
  isOpen: boolean;
  onClose: () => void;
}

export const AboutApp: React.FC<IButtonAboutAppProps> = ({
  isOpen,
  onClose,
}) => {
  return (
    <AboutModal
      isOpen={isOpen}
      onClose={onClose}
      productName={"Chicken Cop"}
      brandImageAlt="Brand Image"
      brandImageSrc={logo}
      backgroundImageSrc={backgroundImage}
      trademark="Copyright © 2020, 2023 by the Chickens"
    >
      <TextContent>
        <TextList component="dl">
          <TextListItem component="dt">Frontend</TextListItem>
          <TextListItem component="dd">
            <TextList component="dl">
              <TextListItem component="dt">Version</TextListItem>
              <TextListItem component="dd">0.1.0</TextListItem>

              <TextListItem component="dt">Git</TextListItem>
              <TextListItem component="dd">
                v0.1.0-nightly.d7fab781-dirty
              </TextListItem>

              <TextListItem component="dt">Build timestamp</TextListItem>
              <TextListItem component="dd">
                2023-06-30T13:21:38.677335558Z
              </TextListItem>
            </TextList>
          </TextListItem>

          <TextListItem component="dt">Backend</TextListItem>
          <TextListItem component="dd">
            <p>https://api.trustification.dev/</p>
            <TextList component="dl">
              <TextListItem component="dt">Version</TextListItem>
              <TextListItem component="dd">0.1.0</TextListItem>

              <TextListItem component="dt">Git</TextListItem>
              <TextListItem component="dd">
                v0.1.0-nightly.d7fab781-dirty
              </TextListItem>

              <TextListItem component="dt">Build timestamp</TextListItem>
              <TextListItem component="dd">
                2023-06-30T13:21:38.677335558Z
              </TextListItem>
            </TextList>
          </TextListItem>
        </TextList>
      </TextContent>
    </AboutModal>
  );
};
