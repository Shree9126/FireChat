package com.mindnotix.mnxchats.eventbus;


public class Events {

	// Event used to send message from fragment to activity.
	public static class FragmentActivityMessage {
		private String message;
		public FragmentActivityMessage(String message) {
			this.message = message;
		}
		public String getMessage() {
			return message;
		}
	}

	// Event used to send message from activity to fragment.
	/*public static class ActivityFragmentMessage {
		private String message;
        private List<ContactRequestTable> inventoryItems;


        public ActivityFragmentMessage(List<ContactRequestTable> inventoryItems) {
            this.inventoryItems = inventoryItems;
        }

        public List<ContactRequestTable> getInventoryItems() {
            return inventoryItems;
        }

        public ActivityFragmentMessage(String message) {
			this.message = message;
		}
		public String getMessage() {
			return message;
		}
	}*/

	// Event used to send message from activity to activity.
	public static class ActivityActivityMessage {
		private String message;
		private String fromname;
		private String count;


		public ActivityActivityMessage(String message, String fromname, String count) {
			this.message = message;
			this.fromname = fromname;
			this.count = count;
		}

		public String getMessage() {
			return message;
		}

		public String getFromname() {
			return fromname;
		}

		public String getCount() {
			return count;
		}
	}

	public static class ChatActivityBlockUnblockStatus {
		private String block_status;
		private int item_position;

		public ChatActivityBlockUnblockStatus(String block_status, int item_position) {
			this.block_status = block_status;
			this.item_position = item_position;
		}

		public String getBlock_status() {
			return block_status;
		}

		public void setBlock_status(String block_status) {
			this.block_status = block_status;
		}

		public int getItem_position() {
			return item_position;
		}

		public void setItem_position(int item_position) {
			this.item_position = item_position;
		}
	}

	public static class ChatActivityMuteImmuteStatus {
		private String mute_status;
		private int item_position;

		public ChatActivityMuteImmuteStatus(String block_status, int item_position) {
			this.mute_status = block_status;
			this.item_position = item_position;
		}


		public String getMute_status() {
			return mute_status;
		}

		public void setMute_status(String mute_status) {
			this.mute_status = mute_status;
		}

		public int getItem_position() {
			return item_position;
		}

		public void setItem_position(int item_position) {
			this.item_position = item_position;
		}
	}


	public static class ActivityGroupImageChange{
		private String GroupImageUrl;
		private String fromname;

		public ActivityGroupImageChange(String groupImageUrl, String fromname) {
			GroupImageUrl = groupImageUrl;
			this.fromname = fromname;
		}


		public String getGroupImageUrl() {
			return GroupImageUrl;
		}

		public void setGroupImageUrl(String groupImageUrl) {
			GroupImageUrl = groupImageUrl;
		}

		public String getFromname() {
			return fromname;
		}

		public void setFromname(String fromname) {
			this.fromname = fromname;
		}
	}


	public static class ChatActivityChangeChatState{
		private String state;
		private String jid;

		public ChatActivityChangeChatState(String state, String jid) {
			this.state = state;
			this.jid = jid;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getJid() {
			return jid;
		}

		public void setJid(String jid) {
			this.jid = jid;
		}
	}


	public static class ChatActivityReferesh{

		private String receipt_id;

		public ChatActivityReferesh(String receipt_id) {
			this.receipt_id = receipt_id;
		}

		public String getReceipt_id() {
			return receipt_id;
		}

		public void setReceipt_id(String receipt_id) {
			this.receipt_id = receipt_id;
		}
	}


	public static class ChatActvityReceiveImages{

		private String jid;
		private String body;
		private long timeMillis;
		private String path;
		private String status;
		private String userType;


		public ChatActvityReceiveImages(String jid, String body, long timeMillis, String path, String s,String userType) {
			this.jid =jid;
			this.body =body;
			this.timeMillis = timeMillis;
			this.path=path;
			this.status=s;
			this.userType=userType;
		}

		public String getJid() {
			return jid;
		}

		public void setJid(String jid) {
			this.jid = jid;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public long getTimeMillis() {
			return timeMillis;
		}

		public void setTimeMillis(long timeMillis) {
			this.timeMillis = timeMillis;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUserType() {
			return userType;
		}

		public void setUserType(String userType) {
			this.userType = userType;
		}
	}
	public static class ChatActivityReceiveMessage{
		private String message_body;
		private String user_jid;
		private String read_count;
		private String timestamp;
		private String type;

	public ChatActivityReceiveMessage(String message_body, String user_jid, String read_count, String timestamp, String type) {
		this.message_body = message_body;
		this.user_jid = user_jid;
		this.read_count = read_count;
		this.timestamp = timestamp;
		this.type = type;
	}

	public String getMessage_body() {
		return message_body;
	}

	public void setMessage_body(String message_body) {
		this.message_body = message_body;
	}

	public String getUser_jid() {
		return user_jid;
	}

	public void setUser_jid(String user_jid) {
		this.user_jid = user_jid;
	}

	public String getRead_count() {
		return read_count;
	}

	public void setRead_count(String read_count) {
		this.read_count = read_count;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}


	public static class ConversationFragementRefresh{

		private int fragmentpostion;
		private int item_position;
		private String fromJID;
		private String lastMessage;


		public ConversationFragementRefresh(int fragmentpostion, int item_position, String fromJID, String lastMessage) {
			this.fragmentpostion = fragmentpostion;
			this.item_position = item_position;
			this.fromJID = fromJID;
			this.lastMessage = lastMessage;
		}

		public int getFragmentpostion() {
			return fragmentpostion;
		}

		public void setFragmentpostion(int fragmentpostion) {
			this.fragmentpostion = fragmentpostion;
		}

		public int getItem_position() {
			return item_position;
		}

		public void setItem_position(int item_position) {
			this.item_position = item_position;
		}

		public String getFromJID() {
			return fromJID;
		}

		public void setFromJID(String fromJID) {
			this.fromJID = fromJID;
		}

		public String getLastMessage() {
			return lastMessage;
		}

		public void setLastMessage(String lastMessage) {
			this.lastMessage = lastMessage;
		}
	}

	public static class ActivityGroupNameChange{
		private String GroupName;
		private String fromname;

		public ActivityGroupNameChange(String groupName, String fromname) {
			GroupName = groupName;
			this.fromname = fromname;
		}

		public String getFromname() {
			return fromname;
		}

		public void setFromname(String fromname) {
			this.fromname = fromname;
		}

		public String getGroupName() {
			return GroupName;
		}

		public void setGroupName(String groupName) {
			GroupName = groupName;
		}
	}



	public static class MyProfilePictureChange {
		private String profileImg;

		public MyProfilePictureChange(String profileImg) {
			this.profileImg = profileImg;

		}

		public String getProfileImg() {
			return profileImg;
		}

		public void setProfileImg(String profileImg) {
			this.profileImg = profileImg;
		}


	}

	public static class ActivityUserProfileChange {
		private String profileImg;
		private String fromname;


		public ActivityUserProfileChange(String profileImg, String fromname) {
			this.profileImg = profileImg;
			this.fromname = fromname;
		}

		public String getProfileImg() {
			return profileImg;
		}

		public void setProfileImg(String profileImg) {
			this.profileImg = profileImg;
		}

		public String getFromname() {
			return fromname;
		}

		public void setFromname(String fromname) {
			this.fromname = fromname;
		}
	}
		public static class ActivityUserStatusChange{
		private String Status;
		private String fromname;

		public ActivityUserStatusChange(String groupName, String fromname) {
			Status = groupName;
			this.fromname = fromname;
		}

		public String getFromname() {
			return fromname;
		}

		public void setFromname(String fromname) {
			this.fromname = fromname;
		}

		public String getStatus() {
			return Status;
		}

		public void setStatus(String status) {
			Status = status;
		}
	}

	// Event used to send message from activity to activity.
	public static class ActivityFragmentRefresh {
		private String message;
		public ActivityFragmentRefresh(String message) {
		this.message =message;
		}


		public String getMessages() {
			return message;
		}
	}
}
