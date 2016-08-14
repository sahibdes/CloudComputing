#!/usr/bin/env python
import sys, json
from datetime import datetime
from pprint import pprint
reload(sys)
sys.setdefaultencoding('utf-8') 	


def format_timestamp(timestamp):
	#	timestamp format is Sun Apr 21 19:08:05 +0000 2014
	#	output format is yyyy-mm-dd
	temp = timestamp.split()
	if len(temp) == 6:	#	check format correctness
		try:
			timestamp = str(datetime.strptime(timestamp,'%a %b %d %H:%M:%S +0000 %Y')).split()[0]
			# timestamp = str(timestamp).split()[0]
			# print timestamp
			return timestamp
		except ValueError:
			return "FormatError"	#check timestamp fields are valid
	else:
		return "FormatError"

for line in sys.stdin:
	try:
		jsondata = json.loads(line)
		# pprint(jsondata)	
	except ValueError:
		continue	#skip malformed data
	try:
		timestamp = jsondata["created_at"]
	except KeyError:
		continue	#skip malformed data
	try:
		tweet_id = jsondata["id"]
	except KeyError:
		continue	#skip malformed data
	try:
		user_id = jsondata["user"]["id"]
	except KeyError:
		continue	#skip malformed data
	try:
		friends_count = jsondata["user"]["friends_count"]
	except KeyError:
		continue	#skip malformed data
	try:
		followers_count = jsondata["user"]["followers_count"]
	except KeyError:
		continue	#skip malformed data

	timestamp = format_timestamp(timestamp)
	if timestamp == "FormatError":
		continue	#skip if timestamp format incorrect
	else:
		reducer_key = str(user_id)+" "+str(timestamp)
		# print reducer_key
	reducer_value = str(tweet_id)+" "+str(friends_count)+" "+str(followers_count)
	print (reducer_key+'\t'+reducer_value)


