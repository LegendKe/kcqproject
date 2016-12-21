package com.djx.pin.xmlprase;

import com.djx.pin.beans.CityModel;
import com.djx.pin.beans.DistrictModel;
import com.djx.pin.beans.ProvinceModule;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Administrator on 2016/6/13.
 */
public class XmlParserHandler extends DefaultHandler {


    /**
     * 存储所有的解析对象
     */
    private List<ProvinceModule> provinceList = new ArrayList<ProvinceModule>();

    public XmlParserHandler() {

    }

    public List<ProvinceModule> getDataList() {
        return provinceList;
    }

    ProvinceModule pm;
    CityModel cm;
    DistrictModel dm;

    /**开始文档解析*/
    // 当读到第一个开始标签的时候，会触发这个方法
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }



    /**开始元素解析*/
    // 当遇到开始标记的时候，调用这个方法
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(qName.equals("province")){
            pm =new ProvinceModule();
            pm.setName(attributes.getValue(0));
            pm.setCityList(new ArrayList<CityModel>());
        }else if (qName.equals("city")){
            cm =new CityModel();
            cm.setName(attributes.getValue(0));
            cm.setDistrictList(new ArrayList<DistrictModel>());
        }else if (qName.equals("district")) {
            dm = new DistrictModel();
            dm.setName(attributes.getValue(0));
        }
    }

    /**结束文档解析*/
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
    /**
     * 结束元素解析
     * uri是名称空间
     localName是包含名称空间的标签，如果没有名称空间，则为空
     qName是不包含名称空间的标签 */
    // 遇到结束标记的时候，会调用这个方法
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (qName.equals("district")) {
            cm.getDistrictList().add(dm);
        } else if (qName.equals("city")) {
            pm.getCityList().add(cm);
        } else if (qName.equals("province")) {
            provinceList.add(pm);
        }
    }
    /** 此方法有三个参数
     ch是传回来的字符数组，其包含元素内容
     start和length分别是数组的开始位置和结束位置 **/
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }
}
