import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class model {

	static int t = 0;
	static int n = 0;

	public static class MyMap extends
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text> {

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			Text keyText = new Text();
			Text valText = new Text();

			String[] arrWords = line.replaceAll("\t", " ").split(" ");

			if (Integer.valueOf(arrWords[arrWords.length - 1]) > 2) {
				// break and get the last word
				String key1 = "";
				for (int i = 0; i < arrWords.length - 2; i++)
					key1 += arrWords[i] + " ";

				String value1 = arrWords.length > 2 ? arrWords[arrWords.length - 2]
						+ "_" + arrWords[arrWords.length - 1]
						: arrWords[arrWords.length - 1];

				if (arrWords.length > 2) {
					keyText.set(key1.trim());
					valText.set(value1.trim());
					context.write(keyText, valText);
				}

				String key2 = "";
				for (int i = 0; i < arrWords.length - 1; i++)
					key2 += arrWords[i] + " ";

				value1 = arrWords[arrWords.length - 1];

				keyText.set(key2.trim());
				valText.set(value1.trim());
				context.write(keyText, valText);
			}
		}
	}

	public static class MyReduce extends
			TableReducer<Text, Text, ImmutableBytesWritable> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			String[] arrList;
			HashMap<String, Integer> hmapWords = new HashMap<String, Integer>();
			TreeMap<String, Double> hmapWordWtihProbs = new TreeMap<String, Double>();

			int denom = 0;
			double conditProb = 0;

			for (Text val : values) {
				if (val.toString().contains("_")) {
					arrList = val.toString().split("_");
					hmapWords.put(arrList[0], Integer.valueOf(arrList[1]));
				} else
					denom = Integer.valueOf(val.toString());
			}

			// calculate condition probs
			for (Entry<String, Integer> entry : hmapWords.entrySet()) {
				if (denom != 0) {
					conditProb = (double) entry.getValue() / denom;
					hmapWordWtihProbs.put(entry.getKey(), conditProb);
				}
			}

			Put put = new Put(Bytes.toBytes(key.toString()));

			int i = 1;
			// now find top 5 based on values and send to database
			for (Entry<String, Double> entry : sortMap(hmapWordWtihProbs)
					.entrySet()) {
				if (i <= 5) {
					put.add(Bytes.toBytes("cf"), Bytes.toBytes(entry.getKey()),
							Bytes.toBytes(entry.getValue().toString()));
					context.write(null, put);
					i++;
				} else
					break;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = HBaseConfiguration.create();
		Job job = new Job(conf, "WordCount_1");
		t = Integer.valueOf(args[2]);
		n = Integer.valueOf(args[3]);
		job.setJarByClass(model.class);

		job.setMapperClass(MyMap.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil
				.initTableReducerJob("ngrams", MyReduce.class, job);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		job.waitForCompletion(true);
	}

	// Method to sort map by values
	@SuppressWarnings({ "rawtypes", "hiding" })
	public static <String extends Comparable, Double extends Comparable> Map<String, Double> sortMap(
			Map<String, Double> map) {
		List<Map.Entry<String, Double>> mapEntries = new LinkedList<Map.Entry<String, Double>>(
				map.entrySet());

		Collections.sort(mapEntries,
				new Comparator<Map.Entry<String, Double>>() {

					@SuppressWarnings("unchecked")
					@Override
					public int compare(Entry<String, Double> val1,
							Entry<String, Double> val2) {
						return val2.getValue().compareTo(val1.getValue());
					}
				});

		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> entry : mapEntries)
			sortedMap.put(entry.getKey(), entry.getValue());

		return sortedMap;
	}
}
