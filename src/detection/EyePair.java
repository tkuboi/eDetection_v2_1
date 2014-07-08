package detection;

public class EyePair extends RegionGroup{
	public double diffArea;
	public double diffX1;
	public double diffX2;
	public double diffY1;
	public double diffY2;
	public double distX;
	public double distY;
	public double distCenters;
	public double distCorners;
	public boolean isEyes;
	
	public EyePair(int id) {
		super(id);
	}

	public EyePair(int id, RegionInfo region1, RegionInfo region2) {
		super(id);
		
		isEyes = false;
		
		this.addMember(region1);
		this.addMember(region2);
		double area1 = Math.abs((double)(region1.maxX - region1.minX) * (double)(region1.maxY - region1.minY));
		double area2 = Math.abs((double)(region2.maxX - region2.minX) * (double)(region2.maxY - region2.minY));
		diffArea = Math.abs(area1 - area2) / area1;
		diffX1 = Math.abs(((double)(region1.minX - region2.minX)) / (double)(region1.minX + region2.minX) / 2.0);
		diffX2 = Math.abs(((double)(region1.maxX - region2.maxX)) / (double)(region1.maxX + region2.maxX) / 2.0);
		diffY1 = Math.abs(((double)(region1.minY - region2.minY)) / (double)(region1.minY + region2.minY) / 2.0);
		diffY2 = Math.abs(((double)(region1.maxY - region2.maxY)) / (double)(region1.maxY + region2.maxY) / 2.0);
		distX = (region1.maxX + region1.minX) / 2 - (region2.maxX + region2.minX) / 2;
		distY = (region1.maxY + region1.minY) / 2 - (region2.maxY + region2.minY) / 2;
		distCenters = distX * distX + distY * distY;
		minX = (region1.minX <= region2.minX ? region1.minX : region2.minX);
		minY = (region1.minY <= region2.minY ? region1.minY : region2.minY);
		maxX = (region1.maxX >= region2.maxX ? region1.maxX : region2.maxX);
		maxY = (region1.maxY >= region2.maxY ? region1.maxY : region2.maxY);

		distCorners = 0;
		if (Math.abs(region1.minX - region2.minX) + Math.abs(region1.minY - region2.minY)
				<= Math.abs(region1.maxX - region2.maxX) + Math.abs(region1.maxY - region2.maxY))
			distCorners = Math.abs(region1.minX - region2.minX) + Math.abs(region1.minY - region2.minY);
		else
			distCorners = Math.abs(region1.maxX - region2.maxX) + Math.abs(region1.maxY - region2.maxY);

	}

	@Override
	public String toCSV() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.isEyes + ",");
		sb.append(this.minX + ",");
		sb.append(this.minY + ",");
		sb.append(this.maxX + ",");
		sb.append(this.maxY + ",");
		sb.append(this.diffArea + ",");
		sb.append(this.diffX1 + ",");
		sb.append(this.diffX2 + ",");
		sb.append(this.diffY1 + ",");
		sb.append(this.diffY2 + ",");
		sb.append(this.distCenters + ",");
		sb.append(this.distCorners + ",");
		sb.append(this.distX + ",");
		sb.append(this.distY);
		for (RegionInfo member : super.getMembers()) {
			sb.append(",");
			sb.append(member.toCSV());
		}
		return sb.toString();
	}
	
	public static String labels() {
		String labels = "";
		labels += RegionInfo.labels();
		labels += ",";
		labels += RegionInfo.labels();
		labels += ",";
		labels += "minX,";
		labels += "minY,";
		labels += "maxX,";
		labels += "maxY,";
		labels += "diffArea,";
		labels += "diffX1,";
		labels += "diffX2,";
		labels += "diffY1,";
		labels += "diffY2,";
		labels += "distCenters,";
		labels += "distCorners,";
		labels += "distX,";
		labels += "distY";
		return labels;
	}
}
