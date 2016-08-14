import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ngram {

	public static class Map
			extends
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			StringBuilder sb = new StringBuilder();

			if (line.trim().length() > 0) {
				line = line.replace("\"", " ");
				// replace special chars with space
				line = line.toLowerCase().replaceAll("[^A-Za-z]", " ");

				// get each word into array
				String[] arrWords = line.split("\\s+");
				// create n-grams i to 5-grams
				for (int i = 0; i < arrWords.length; i++) {
					if (arrWords[i].trim().length() > 0) {
						sb = new StringBuilder();
						word = new Text();
						sb.append(arrWords[i]);
						word.set(sb.toString());
						context.write(word, one);
						for (int j = i + 1; j < arrWords.length; j++) {
							// pick the first work and go till the last word
							if (j <= i + 4) {
								sb.append(" " + arrWords[j]);
								word.set(sb.toString().trim());
								context.write(word, one);
							}
						}
					}
				}
			}
		}
	}

	public static class Reduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			context.write(key, new IntWritable(sum));
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "mapred");
		job.setJarByClass(ngram.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}
}
