import { PackageDetails } from "@app/api/models";
import { CodeEditor, Language } from "@patternfly/react-code-editor";
import { stringify } from "querystring";
import React from "react";

interface SourceProps {
  packageDetails: PackageDetails;
}

export const Source: React.FC<SourceProps> = ({ packageDetails }) => {
  return (
    <>
      <CodeEditor
        isDarkTheme
        isLineNumbersVisible
        isReadOnly
        isMinimapVisible
        // isLanguageLabelVisible
        code={JSON.stringify(packageDetails, null, 2)}
        language={Language.json}
        height="650px"
      />
    </>
  );
};
