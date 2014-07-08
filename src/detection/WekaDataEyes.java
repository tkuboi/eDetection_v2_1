package detection;

import java.util.List;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class WekaDataEyes extends WekaData implements WekaFormat {
	
	private final int numFields = 78;

	public WekaDataEyes() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public FastVector createVector() {
		FastVector fvClassVal = new FastVector(2);
		fvClassVal.addElement("true");
		fvClassVal.addElement("false");
		Attribute ClassAttribute = new Attribute("theClass", fvClassVal);

		FastVector fvBoolVal = new FastVector(2);
		fvBoolVal.addElement("true");
		fvBoolVal.addElement("false");
		
		Attribute Attribute2 = new Attribute("minX");
		Attribute Attribute3 = new Attribute("minY");
		Attribute Attribute4 = new Attribute("maxX");
		Attribute Attribute5 = new Attribute("maxY");
		Attribute Attribute6 = new Attribute("diffArea");
		Attribute Attribute7 = new Attribute("diffX1");
		Attribute Attribute8 = new Attribute("diffX2");
		Attribute Attribute9 = new Attribute("diffY1");
		Attribute Attribute10 = new Attribute("diffY2");
		Attribute Attribute11 = new Attribute("distCenters");
		Attribute Attribute12 = new Attribute("distCorners");
		Attribute Attribute13 = new Attribute("distX");
		Attribute Attribute14 = new Attribute("distY");
		
		Attribute Attribute74 = new Attribute("maxY2");
		Attribute Attribute75 = new Attribute("pixCount");
		Attribute Attribute76 = new Attribute("xRange");
		Attribute Attribute77 = new Attribute("yRange");
		Attribute Attribute78 = new Attribute("isAreaLessThanMax");
		Attribute Attribute79 = new Attribute("isAreaMoreThanMin");
		Attribute Attribute80 = new Attribute("isXLessThanMax");
		Attribute Attribute81 = new Attribute("isYLessThanMax");
		Attribute Attribute82 = new Attribute("isRegionEnclosed");
		Attribute Attribute83 = new Attribute("isPixCountMoreThanMin");
		Attribute Attribute84 = new Attribute("isDistBtwCentersLessThanMax");
		Attribute Attribute15 = new Attribute("distBtwCenters");
		Attribute Attribute16 = new Attribute("blkPixPercent");
		Attribute Attribute17 = new Attribute("sumPix");
		Attribute Attribute18 = new Attribute("sdHoriz");
		Attribute Attribute19 = new Attribute("sdVert");
		Attribute Attribute20 = new Attribute("sdPix");
		Attribute Attribute21 = new Attribute("enclosed", fvBoolVal);
		Attribute Attribute22 = new Attribute("percentEnclosed");
		Attribute Attribute23 = new Attribute("percentArea");
		Attribute Attribute24 = new Attribute("histH_1");
		Attribute Attribute25 = new Attribute("histH_2");
		Attribute Attribute26 = new Attribute("histH_3");
		Attribute Attribute27 = new Attribute("histH_4");
		Attribute Attribute28 = new Attribute("histH_5");
		Attribute Attribute29 = new Attribute("histH_6");
		Attribute Attribute30 = new Attribute("histH_7");
		Attribute Attribute31 = new Attribute("histH_8");
		Attribute Attribute32 = new Attribute("histH_9");
		Attribute Attribute33 = new Attribute("histH_10");
		Attribute Attribute34 = new Attribute("histV_1");
		Attribute Attribute35 = new Attribute("histV_2");
		Attribute Attribute36 = new Attribute("histV_3");
		Attribute Attribute37 = new Attribute("histV_4");
		Attribute Attribute38 = new Attribute("histV_5");
		Attribute Attribute39 = new Attribute("histV_6");
		Attribute Attribute40 = new Attribute("histV_7");
		Attribute Attribute41 = new Attribute("histV_8");
		Attribute Attribute42 = new Attribute("histV_9");
		Attribute Attribute43 = new Attribute("histV_10");
		Attribute Attribute44 = new Attribute("histR_1");
		Attribute Attribute45 = new Attribute("histR_2");
		Attribute Attribute46 = new Attribute("histR_3");
		Attribute Attribute47 = new Attribute("histR_4");
		Attribute Attribute48 = new Attribute("histR_5");
		Attribute Attribute49 = new Attribute("histR_6");
		Attribute Attribute50 = new Attribute("histR_7");
		Attribute Attribute51 = new Attribute("histR_8");
		Attribute Attribute52 = new Attribute("histR_9");
		Attribute Attribute53 = new Attribute("histR_10");
		Attribute Attribute54 = new Attribute("histR_11");
		Attribute Attribute55 = new Attribute("histR_12");
		Attribute Attribute56 = new Attribute("histR_13");
		Attribute Attribute57 = new Attribute("histR_14");
		Attribute Attribute58 = new Attribute("histR_15");
		Attribute Attribute59 = new Attribute("histR_16");
		Attribute Attribute60 = new Attribute("histR_17");
		Attribute Attribute61 = new Attribute("histR_18");
		Attribute Attribute62 = new Attribute("histR_19");
		Attribute Attribute63 = new Attribute("histR_20");
		Attribute Attribute64 = new Attribute("histR_21");
		Attribute Attribute65 = new Attribute("histR_22");
		Attribute Attribute66 = new Attribute("histR_23");
		Attribute Attribute67 = new Attribute("histR_24");
		
		FastVector fvWekaAttributes = new FastVector(78);
		fvWekaAttributes.addElement(Attribute2);    
		fvWekaAttributes.addElement(Attribute3);    
		fvWekaAttributes.addElement(Attribute4);    
		fvWekaAttributes.addElement(Attribute5);    
		fvWekaAttributes.addElement(Attribute6);    
		fvWekaAttributes.addElement(Attribute7);    
		fvWekaAttributes.addElement(Attribute8);    
		fvWekaAttributes.addElement(Attribute9);    
		fvWekaAttributes.addElement(Attribute10);    
		fvWekaAttributes.addElement(Attribute11);    
		fvWekaAttributes.addElement(Attribute12);    
		fvWekaAttributes.addElement(Attribute13);    
		fvWekaAttributes.addElement(Attribute14);
		fvWekaAttributes.addElement(Attribute74);    
		fvWekaAttributes.addElement(Attribute75);    
		fvWekaAttributes.addElement(Attribute76);    
		fvWekaAttributes.addElement(Attribute77);    
		fvWekaAttributes.addElement(Attribute78);    
		fvWekaAttributes.addElement(Attribute79);    
		fvWekaAttributes.addElement(Attribute80);    
		fvWekaAttributes.addElement(Attribute81);    
		fvWekaAttributes.addElement(Attribute82);    
		fvWekaAttributes.addElement(Attribute83);    
		fvWekaAttributes.addElement(Attribute84);    
		fvWekaAttributes.addElement(Attribute15);    
		fvWekaAttributes.addElement(Attribute16);    
		fvWekaAttributes.addElement(Attribute17);    
		fvWekaAttributes.addElement(Attribute18);    
		fvWekaAttributes.addElement(Attribute19);    
		fvWekaAttributes.addElement(Attribute20);    
		fvWekaAttributes.addElement(Attribute21);    
		fvWekaAttributes.addElement(Attribute22);    
		fvWekaAttributes.addElement(Attribute23);    
		fvWekaAttributes.addElement(Attribute24);    
		fvWekaAttributes.addElement(Attribute25);    
		fvWekaAttributes.addElement(Attribute26);    
		fvWekaAttributes.addElement(Attribute27);    
		fvWekaAttributes.addElement(Attribute28);    
		fvWekaAttributes.addElement(Attribute29);    
		fvWekaAttributes.addElement(Attribute30);
		fvWekaAttributes.addElement(Attribute31);    
		fvWekaAttributes.addElement(Attribute32);    
		fvWekaAttributes.addElement(Attribute33);    
		fvWekaAttributes.addElement(Attribute34);    
		fvWekaAttributes.addElement(Attribute35);    
		fvWekaAttributes.addElement(Attribute36);    
		fvWekaAttributes.addElement(Attribute37);    
		fvWekaAttributes.addElement(Attribute38);    
		fvWekaAttributes.addElement(Attribute39);    
		fvWekaAttributes.addElement(Attribute40);    
		fvWekaAttributes.addElement(Attribute41);    
		fvWekaAttributes.addElement(Attribute42);    
		fvWekaAttributes.addElement(Attribute43);    
		fvWekaAttributes.addElement(Attribute44);    
		fvWekaAttributes.addElement(Attribute45);    
		fvWekaAttributes.addElement(Attribute46);    
		fvWekaAttributes.addElement(Attribute47);    
		fvWekaAttributes.addElement(Attribute48);    
		fvWekaAttributes.addElement(Attribute49);    
		fvWekaAttributes.addElement(Attribute50);    
		fvWekaAttributes.addElement(Attribute51);    
		fvWekaAttributes.addElement(Attribute52);    
		fvWekaAttributes.addElement(Attribute53);    
		fvWekaAttributes.addElement(Attribute54);    
		fvWekaAttributes.addElement(Attribute55);    
		fvWekaAttributes.addElement(Attribute56);    
		fvWekaAttributes.addElement(Attribute57);    
		fvWekaAttributes.addElement(Attribute58);    
		fvWekaAttributes.addElement(Attribute59);    
		fvWekaAttributes.addElement(Attribute60);    
		fvWekaAttributes.addElement(Attribute61);    
		fvWekaAttributes.addElement(Attribute62);    
		fvWekaAttributes.addElement(Attribute63);    
		fvWekaAttributes.addElement(Attribute64);    
		fvWekaAttributes.addElement(Attribute65);    
		fvWekaAttributes.addElement(Attribute66);    
		fvWekaAttributes.addElement(Attribute67);
		fvWekaAttributes.addElement(ClassAttribute);

		return fvWekaAttributes;
	}

	@Override
	public Instances createInstances(FastVector fvWekaAttributes,
			List<String> strArray) {
		Instances trainningSet = new Instances("Rel", fvWekaAttributes, 78);
		String[] tokens;
		for (String str : strArray) {
			//System.out.println(str);
			tokens = str.split(",");
			Instance vector = new Instance(78);
			vector.setValue((Attribute)fvWekaAttributes.elementAt(0), Double.parseDouble(tokens[2].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(1), Double.parseDouble(tokens[3].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(2), Double.parseDouble(tokens[4].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(3), Double.parseDouble(tokens[5].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(4), Double.parseDouble(tokens[6].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(5), Double.parseDouble(tokens[7].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(6), Double.parseDouble(tokens[8].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(7), Double.parseDouble(tokens[9].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(8), Double.parseDouble(tokens[10].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(9), Double.parseDouble(tokens[11].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(10), Double.parseDouble(tokens[12].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(11), Double.parseDouble(tokens[13].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(12), Double.parseDouble(tokens[14].trim()));
			
			vector.setValue((Attribute)fvWekaAttributes.elementAt(13), Double.parseDouble(tokens[20].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(14), Double.parseDouble(tokens[21].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(15), Double.parseDouble(tokens[22].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(16), Double.parseDouble(tokens[23].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(17), Double.parseDouble(tokens[24].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(18), Double.parseDouble(tokens[25].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(19), Double.parseDouble(tokens[26].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(20), Double.parseDouble(tokens[27].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(21), Double.parseDouble(tokens[28].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(22), Double.parseDouble(tokens[29].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(23), Double.parseDouble(tokens[30].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(24), Double.parseDouble(tokens[31].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(25), Double.parseDouble(tokens[32].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(26), Double.parseDouble(tokens[33].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(27), Double.parseDouble(tokens[34].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(28), Double.parseDouble(tokens[35].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(29), Double.parseDouble(tokens[36].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(30), tokens[37].trim());
			vector.setValue((Attribute)fvWekaAttributes.elementAt(31), Double.parseDouble(tokens[38].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(32), Double.parseDouble(tokens[39].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(33), Double.parseDouble(tokens[40].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(34), Double.parseDouble(tokens[41].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(35), Double.parseDouble(tokens[42].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(36), Double.parseDouble(tokens[43].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(37), Double.parseDouble(tokens[44].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(38), Double.parseDouble(tokens[45].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(39), Double.parseDouble(tokens[46].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(40), Double.parseDouble(tokens[47].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(41), Double.parseDouble(tokens[48].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(42), Double.parseDouble(tokens[49].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(43), Double.parseDouble(tokens[50].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(44), Double.parseDouble(tokens[51].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(45), Double.parseDouble(tokens[52].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(46), Double.parseDouble(tokens[53].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(47), Double.parseDouble(tokens[54].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(48), Double.parseDouble(tokens[55].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(49), Double.parseDouble(tokens[56].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(50), Double.parseDouble(tokens[57].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(51), Double.parseDouble(tokens[58].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(52), Double.parseDouble(tokens[59].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(53), Double.parseDouble(tokens[60].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(54), Double.parseDouble(tokens[61].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(55), Double.parseDouble(tokens[62].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(56), Double.parseDouble(tokens[63].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(57), Double.parseDouble(tokens[64].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(58), Double.parseDouble(tokens[65].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(59), Double.parseDouble(tokens[66].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(60), Double.parseDouble(tokens[67].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(61), Double.parseDouble(tokens[68].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(62), Double.parseDouble(tokens[69].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(63), Double.parseDouble(tokens[70].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(64), Double.parseDouble(tokens[71].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(65), Double.parseDouble(tokens[72].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(66), Double.parseDouble(tokens[73].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(67), Double.parseDouble(tokens[74].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(68), Double.parseDouble(tokens[75].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(69), Double.parseDouble(tokens[76].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(70), Double.parseDouble(tokens[77].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(71), Double.parseDouble(tokens[78].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(72), Double.parseDouble(tokens[79].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(73), Double.parseDouble(tokens[80].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(74), Double.parseDouble(tokens[81].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(75), Double.parseDouble(tokens[82].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(76), Double.parseDouble(tokens[83].trim()));

			vector.setValue((Attribute)fvWekaAttributes.elementAt(77), tokens[1].trim());
			trainningSet.add(vector);
		}
		int cIdx=trainningSet.numAttributes()-1;
		trainningSet.setClassIndex(cIdx);

        return trainningSet;
	}

	@Override
	public void setFields(String str) {
		// TODO Auto-generated method stub
		
	}

}
