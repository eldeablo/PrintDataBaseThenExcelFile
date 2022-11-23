package com.project.Incom;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class BDPrinter {

    private final JProgressBar _progressBar;
    private String _nameWire;

    public BDPrinter(JProgressBar bar) {
        this._progressBar = bar;
    }

    /**
     * Create xml file
     */
    public void createXMLDB(String outFolder, String printData, String nameFiles) {
        setNameWire(printData);
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("ImageDesign");
            document.appendChild(root);
            String nameIn = nameFiles + "(" + _nameWire + ")";
            root.setAttribute("xml:space", "default");
            root.setAttribute("InternalName", nameIn);

            Element genericSetting = document.createElement("GenericSettings");
            root.appendChild(genericSetting);

            Element imageMapping = document.createElement("ImageMapping");
            genericSetting.appendChild(imageMapping);

            Element map = document.createElement("Map");
            map.setAttribute("SubImageRef", "1");
            map.setTextContent("1");
            imageMapping.appendChild(map);

            Element parameters = document.createElement("Parameters");
            genericSetting.appendChild(parameters);
            Element paramSet = document.createElement("ParamSet");
            paramSet.setAttribute("Name", "CJ400 CIJ");
            paramSet.setAttribute("Issue", "2");
            parameters.appendChild(paramSet);

            Element param = document.createElement("Param");
            param.setAttribute("Name", "Raster");
            param.setAttribute("Type", "Str");

            CDATASection cdataSection = document.createCDATASection("1x5 Std Linear");
            param.appendChild(cdataSection);
            paramSet.appendChild(param);

            createParamName(paramSet, document, "Pitch", "Num", 469);
            createParamName(paramSet, document, "Delay", "Num", 0);
            createParamName(paramSet, document, "DeltaHeight", "Num", 0);
            createParamName(paramSet, document, "PrintCount", "Num", 0);

            Element paramImage = document.createElement("Param");
            paramImage.setAttribute("Name", "ProductImage");
            paramImage.setAttribute("Type", "Str");

            CDATASection cdataSectionImage = document.createCDATASection(" ");
            paramImage.appendChild(cdataSectionImage);
            paramSet.appendChild(paramImage);

            createParamName(paramSet, document, "autoload_cache", "Str", -2);
            createParamName(paramSet, document, "remote_field_info", "Str", -2);


            Element header = document.createElement("Header");
            header.setAttribute("DesignedFor", "CJ400 CIJ");
            root.appendChild(header);

            Element units = document.createElement("Units");
            units.setTextContent("hmm");
            header.appendChild(units);

            Element subImage = document.createElement("SubImage");
            subImage.setAttribute("ImageReference", "1");
            root.appendChild(subImage);

            Element subHeader = document.createElement("SubHeader");
            subHeader.setAttribute("FormatName", "1x5 western fixed wide");
            subImage.appendChild(subHeader);

            createParamTextContext(subHeader, document, "ImageWidth", "6971");
            createParamTextContext(subHeader, document, "ImageHeight", "182");
            createParamTextContext(subHeader, document, "XRes", "0.02740");
            createParamTextContext(subHeader, document, "MaxImageHeight", "182");
            createParamTextContext(subHeader, document, "ImageRotation", "0");
            createParamTextContext(subHeader, document, "ImageMultiStroke", "1");
            createParamTextContext(subHeader, document, "ImageMirror", "FALSE");
            createParamTextContext(subHeader, document, "ImageInverse", "FALSE");

            Element field = document.createElement("Field");
            field.setAttribute("Name", "Field 1");
            subImage.appendChild(field);

            createParamTextContext(field, document, "X", "0");
            createParamTextContext(field, document, "Y", "0");
            createParamTextContext(field, document, "W", "6971");
            createParamTextContext(field, document, "H", "182");
            createParamTextContext(field, document, "Orientation", "0");
            createParamTextContext(field, document, "Mirror", "FALSE");
            createParamTextContext(field, document, "Inverse", "FALSE");
            createParamTextContext(field, document, "MultiStroke", "1");
            createParamTextContext(field, document, "InterCharacterGap", "-1");
            createParamTextContext(field, document, "FldType", "FixedText");

            Element calcData = document.createElement("CalcData");
            CDATASection cdataSectionCalc = document.createCDATASection(printData);
            calcData.appendChild(cdataSectionCalc);
            field.appendChild(calcData);

            Element data = document.createElement("Data");
            field.appendChild(data);

            Element objectIndex = document.createElement("Object");
            objectIndex.setAttribute("Index", "0");
            data.appendChild(objectIndex);

            createParamTextContext(objectIndex, document, "DataType", "0");
            createParamTextContext(objectIndex, document, "MaxNoOfChars", "32");
            createParamTextContext(objectIndex, document, "MinNoOfChars", "1");

            Element defaultS = document.createElement("Default");
            CDATASection defaultCD = document.createCDATASection(printData);
            defaultS.appendChild(defaultCD);
            objectIndex.appendChild(defaultS);


            Element varText = document.createElement("VarText");
            objectIndex.appendChild(varText);

            Element prompt = document.createElement("Prompt");
            CDATASection promptCD = document.createCDATASection("Введите данные");
            prompt.appendChild(promptCD);
            varText.appendChild(prompt);

            Element userEnterData = document.createElement("UserEnterData");
            CDATASection userEnterDataCD = document.createCDATASection(printData);
            userEnterData.appendChild(userEnterDataCD);
            varText.appendChild(userEnterData);


            createParamTextContext(varText, document, "PromptAtCoder", "FALSE");
            createParamTextContext(varText, document, "ResetOnSel", "ContinueWithCurrentVal");


            Element text = document.createElement("Text");
            field.appendChild(text);

            Element font = document.createElement("Font");
            text.appendChild(font);

            Element face = document.createElement("Face");
            CDATASection faceCD = document.createCDATASection("Linx 5 High");
            face.appendChild(faceCD);
            font.appendChild(face);

            createParamTextContext(font, document, "Family", "Modern");
            createParamTextContext(font, document, "Pitch", "5");

            createParamTextContext(text, document, "Bold", "5");
            createParamTextContext(text, document, "Escapement", "5");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSource = new DOMSource(document);

            createFolder(outFolder);
            StreamResult streamResult = new StreamResult(new File(
                    outFolder + "\\Linx\\8920\\Messages" + "\\" + nameFiles + "(" + _nameWire + ")" + ".Message.xml"));

            transformer.transform(domSource, streamResult);
            _progressBar.setValue(_progressBar.getValue() + 1);

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

    private void createFolder(String outFolder) {
        File createFolder = new File(outFolder + "\\Linx\\8920\\Messages");
        createFolder.mkdirs();
    }

    private void createParamName(Element root, Document document, String name, String type, int text) {
        Element param = document.createElement("Param");
        param.setAttribute("Name", name);
        param.setAttribute("Type", type);

        if (text == -1 && type.contains("Str")) {
            CDATASection cdataSection = document.createCDATASection("");
            param.appendChild(cdataSection);
        }
        else if (type.contains("Str") && text == -2) {
            param.setTextContent("_");
        }
        else {
            param.setTextContent(String.valueOf(text));
        }

        root.appendChild(param);

    }

    /**
     * Create doc is set content
     */
    private void createParamTextContext(Element root, Document document, String nameElement, String text) {
        Element subParam = document.createElement(nameElement);
        subParam.setTextContent(text);
        root.appendChild(subParam);
    }


    private void setNameWire(String nameWire) {
        String[] listWire = nameWire.split("\\s");
        _nameWire = listWire[0];
    }
}
