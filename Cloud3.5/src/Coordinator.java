import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Queue;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.sql.Timestamp;
import java.util.Map.Entry;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

import java.util.concurrent.BlockingQueue;

public class Coordinator extends Verticle {

	// Default mode: Strongly consistent. Possible values are "strong" and
	// "causal"
	private static String consistencyType = "strong";

	BlockingQueue<custom> queue = new ArrayBlockingQueue<custom>(1024, true);

	/**
	 * TODO: Set the values of the following variables to the DNS names of your
	 * three dataCenter instances
	 */
	private static final String dataCenter1 = "ec2-52-0-21-38.compute-1.amazonaws.com";
	private static final String dataCenter2 = "ec2-52-5-95-227.compute-1.amazonaws.com";
	private static final String dataCenter3 = "ec2-52-5-20-155.compute-1.amazonaws.com";

	// ConcurrentHashMap<String, ReentrantLock> conMapKey = new
	// ConcurrentHashMap<String, ReentrantLock>();

	@Override
	public void start() {
		// Collections.synchronizedCollection(queue);
		// DO NOT MODIFY THIS
		KeyValueLib.dataCenters.put(dataCenter1, 1);
		KeyValueLib.dataCenters.put(dataCenter2, 2);
		KeyValueLib.dataCenters.put(dataCenter3, 3);
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);

		routeMatcher.get("/put", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();

				final String key = map.get("key");
				final String value = map.get("value");
				// You may use the following timestamp for ordering requests
				final String timestamp = new Timestamp(System
						.currentTimeMillis()
						+ TimeZone.getTimeZone("EST").getRawOffset())
						.toString();

				custom c = new custom(key, value, timestamp, "put");
				synchronized (queue) {
					queue.add(c);
				}

				Thread t = new Thread(new Runnable() {
					public void run() {
						// for strong consistency
						try {
							custom objC = null;
							// TODO: Write code for PUT operation here.
							// Each PUT operation is handled in a different
							// thread.
							// Highly recommended that you make use of
							// helper
							// functions

							if (consistencyType.equals("strong")) {
								Object[] arrcustoms = queue.toArray();

								for (int i = 0; i < arrcustoms.length; i++) {
									if (((custom) (arrcustoms[i])).keyVal
											.equals(key)) {
										objC = (custom) (arrcustoms[i]);
										break;
									}
								}

								String keyVa1l = objC.keyVal;
								String val1 = objC.valueVal;
								synchronized (queue) {
									while (!objC.timeStampVal.equals(timestamp)) {
										queue.wait();
									}

									KeyValueLib.PUT(dataCenter1, keyVa1l, val1);
									KeyValueLib.PUT(dataCenter2, keyVa1l, val1);
									KeyValueLib.PUT(dataCenter3, keyVa1l, val1);

									queue.remove(objC);
									queue.notifyAll();
								}
							} else if (consistencyType.equals("causal")) {

								synchronized (queue) {
									objC = queue.poll();
								}

								String keyVa1l = objC.keyVal;
								String val1 = objC.valueVal;

								KeyValueLib.PUT(dataCenter1, keyVa1l, val1);
								KeyValueLib.PUT(dataCenter2, keyVa1l, val1);
								KeyValueLib.PUT(dataCenter3, keyVa1l, val1);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				t.start();
				req.response().end(); // Do not remove this
			}
		});

		routeMatcher.get("/get", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String loc = map.get("loc");

				// You may use the following timestamp for ordering requests
				final String timestamp = new Timestamp(System
						.currentTimeMillis()
						+ TimeZone.getTimeZone("EST").getRawOffset())
						.toString();
				custom c = new custom(key, loc, timestamp, "get");

				synchronized (queue) {
					queue.add(c);
				}

				Thread t = new Thread(new Runnable() {
					public void run() {
						custom obj2 = null;
						// TODO: Write code for GET operation here.
						// Each GET operation is handled in a different
						// thread.
						// Highly recommended that you make use of helper
						// functions.
						try {

							if (consistencyType.equals("strong")) {
								Object[] arrcustoms = queue.toArray();

								for (int i = 0; i < arrcustoms.length; i++) {
									if (((custom) (arrcustoms[i])).keyVal
											.equals(key)) {
										obj2 = (custom) (arrcustoms[i]);
										break;
									}
								}

								String keyVa12 = obj2.keyVal;
								String val2 = obj2.valueVal;

								synchronized (queue) {
									while (!obj2.timeStampVal.equals(timestamp)) {
										queue.wait();
									}
									String response = KeyValueLib.GET(
											val2.equals("1") ? dataCenter1
													: val2.equals("2") ? dataCenter2
															: dataCenter3,
											keyVa12);

									req.response().end(response);
									queue.remove(obj2);
									queue.notifyAll();
								}
							} else if (consistencyType.equals("causal")) {

								synchronized (queue) {
									obj2 = queue.poll();
								}
								String keyVa12 = obj2.keyVal;
								String val2 = obj2.valueVal;
								String response = KeyValueLib.GET(
										val2.equals("1") ? dataCenter1 : val2
												.equals("2") ? dataCenter2
												: dataCenter3, keyVa12);
								req.response().end(response);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				t.start();
			}
		});

		routeMatcher.get("/consistency", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				consistencyType = map.get("consistency");
				// This endpoint will be used by the auto-grader to set the
				// consistency type that your key-value store has to support.
				// You can initialize/re-initialize the required data structures
				// here
				queue = new ArrayBlockingQueue<custom>(1024, true);
				req.response().end();
			}
		});

		routeMatcher.noMatch(new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().putHeader("Content-Type", "text/html");
				String response = "Not found.";
				req.response().putHeader("Content-Length",
						String.valueOf(response.length()));
				req.response().end(response);
				req.response().close();
			}
		});
		server.requestHandler(routeMatcher);
		server.listen(8080);
	}
}

class custom {
	String keyVal;
	String valueVal;
	String timeStampVal;
	String typeVal;

	public custom(String keyParam, String valueParam, String timeStampParam,
			String typeParam) {
		super();
		this.keyVal = keyParam;
		this.valueVal = valueParam;
		this.timeStampVal = timeStampParam;
		this.typeVal = typeParam;
	}
}
