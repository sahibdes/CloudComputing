#!/usr/bin/env python
import sys

'''
sample reducer input - 566145686 2014-04-13    455422188213272576 229 297
					   user_id timestamp <tab> tweet_id friends followers

Unique tweets are assumed to be distinguished by unique tweet_id's
'''

current_key = ""
current_value = ""
tweet_id_dict = {}	#	format is {tweet id: {friends, followers}} for unique user_id-timestamp - to check duplicate tweets
tweets_score = 0

def max(tweet_id_dict,counter):
	max_count = 0
	for key1 in tweet_id_dict:
		count = int(tweet_id_dict[key1][counter])
		if max_count < count:
			max_count = count
	# print max_count
	return max_count


for line in sys.stdin:
	try:
		if len(line.split("\t")) == 2:	#check input format correctness
			input_key = line.split("\t")[0]	#input key
			input_value = line.split("\t")[1]	#input value
		else:
			continue
	except IndexError:
		continue

	if input_key == current_key and current_key != "":
		current_value = input_value
		values = current_value.split()
		# print str(values[0])

		if str(values[0]) in tweet_id_dict:	#not unique tweet for this key
			pass
		else:
			tweet_id_dict[str(values[0])] = {"friends": int(values[1]),"followers": int(values[2])}
			tweets_score += 1

	elif input_key != current_key and current_key != "":	#next user_id-timestamp
		# print tweets_score	
		# print tweet_id_dict
		for key in tweet_id_dict:	#prints user_id,timestamp,tweet_score,friends_score,followers_score- tab separated values, after getting max(friends and followers)
			friends_count = max(tweet_id_dict,"friends")
			followers_count = max(tweet_id_dict,"followers")
			friends_score = (friends_count*3)
			followers_score = (followers_count*5)
		print str(current_key.split()[0])+"\t"+str(current_key.split()[1])+"\t"+str(tweets_score)+"\t"+str(friends_score)+"\t"+str(followers_score)
		#	mapper output done, reset values for new user_id timestamp
		current_key = input_key
		current_value = input_value
		tweet_id_dict = {}	
		tweets_score = 0	
		friends_count = 0	
		followers_count = 0
		friends_score = 0
		followers_score = 0
		values = current_value.split()
		if str(values[0]) in tweet_id_dict:	#not unique tweet for this key
			pass
		else:
			tweet_id_dict[str(values[0])] = {"friends": int(values[1]),"followers": int(values[2])}
			tweets_score += 1

	elif current_key == "" and current_value == "":	#for the first line
		current_key = input_key
		current_value = input_value
		values = current_value.split()
		if str(values[0]) in tweet_id_dict.keys():	#unique tweet for this key
			pass
		else:
			tweet_id_dict[str(values[0])] = {"friends": int(values[1]),"followers": int(values[2])}
			tweets_score += 1

#last line condition
if input_key == current_key and current_key != "":	#last line is previous user_id-timestamp
	# print current_key
	current_value = input_value
	# print current_value
	values = current_value.split()
	# print str(values[0])
	tweet_id_dict[str(values[0])] = {"friends": int(values[1]),"followers": int(values[2])}
	# print tweet_id_dict
	for key in tweet_id_dict:	#prints user_id,timestamp,tweet_score,friends_score,followers_score- tab separated values, after getting max(friends and followers)
		friends_count = max(tweet_id_dict,"friends")
		followers_count = max(tweet_id_dict,"followers")
		friends_score = (friends_count*3)
		followers_score = (followers_count*5)
	print str(current_key.split()[0])+"\t"+str(current_key.split()[1])+"\t"+str(tweets_score)+"\t"+str(friends_score)+"\t"+str(followers_score)
elif input_key != current_key and current_key != "":	#last line is new user_id-timestamp
	# print current_key
	current_key = input_key
	current_value = input_value
	tweet_id_dict = {}	
	tweets_score = 0	
	friends_count = 0	
	followers_count = 0
	friends_score = 0
	followers_score = 0
	values = current_value.split()
	tweet_id_dict[str(values[0])] = {"friends": int(values[1]),"followers": int(values[2])}
	tweets_score += 1	
	# print tweet_id_dict
	for key in tweet_id_dict:	#prints user_id,timestamp,tweet_score,friends_score,followers_score- tab separated values, after getting max(friends and followers)
		friends_count = tweet_id_dict["friends"]
		followers_count = tweet_id_dict["followers"]
		friends_score = (friends_count*3)
		followers_score = (followers_count*5)
	print str(current_key.split()[0])+"\t"+str(current_key.split()[1])+"\t"+str(tweets_score)+"\t"+str(friends_score)+"\t"+str(followers_score)
else:
	pass
