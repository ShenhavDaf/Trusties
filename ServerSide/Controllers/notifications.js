const User = require("../Models/user_model");
const Post = require("../Models/post_model");
const Sos = require("../Models/sos_model");
const Comment = require("../Models/comment_model");
const Notification = require("../Models/notification_model");
const { object } = require("mongoose/lib/utils");
const request = require("request");
const admin = require("firebase-admin");
const serviceAccount = require("../trusties-firebase-adminsdk.json");


const getAllNotifications = async (req, res, next) => {
  console.log("get all notifications");
  Notification.find({}, function (err, docs) {
    if (err) console.log(err);
    else res.status(200).send(docs);
  });
};

const addNotification = async (req, res, next) => {
  console.log("add new notification ");

  const post = await Post.findOne({ _id: req.body.post });
  const user = await User.findOne({ email: req.body.sender });
  const time = req.body.time;
  const type = req.body.type;
  const circle = req.body.circle;


  const notification = Notification({
    sender: user,
    post: post,
    time: Date.now(),
    type: type,
    circle: circle
  });

  notification.save(async (error, notification) => {
    if (error) {
      res.status(400).send({
        status: "fail",
        error: error.message,
      });
    } else {
      res.status(200).send({
        status: "OK",
        notification: notification,
      });
    }
  });
};

// ## New version - Not fully Working. 
const sendNotification = async (req, res, next) => {
  const post = await Post.findOne({ _id: req.body.post });
  const token_db = (await User.findOne({ _id: post.sender })).firebaseToken;

  const token = [token_db];


  const options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
  };

  const payload = {
    notification: {
      title: "Test Notification",
      body: "Hey..."
    }
  }
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  });

  admin.messaging().sendToDevice(token, payload, options)
    .then(response => {

      console.log("## Inside admin.messaging()");
      console.log(response);
      res.status(200).send("OK");

    })
    .catch(error => {
      console.log(error);
    });
  // request.post({
  //   headers: { 'content-type': 'application/json', "Authorization": process.env.FIREBASE_TOKEN },
  //   url: "https://fcm.googleapis.com/fcm/send",
  //   body: payload,
  //   json: true,
  // });
};

// ## Old version

// const sendNotification = async (req, res, next) => {
//   console.log("Send notification");
//   console.log(req.body);
//   const post = await Post.findOne({ _id: req.body.post });
//   const token = (await User.findOne({ _id: post.sender })).firebaseToken;
//   console.log("token: " + token);

//   const payload = {
//     to: token,
//     notification: {
//       title: "Test Notification",
//       body: "Hey..."
//     }
//   }

//   request.post({
//     headers: { 'content-type': 'application/json', "Authorization": process.env.FIREBASE_TOKEN },
//     url: "https://fcm.googleapis.com/fcm/send",
//     body: payload,
//     json: true,
//   });
// };


module.exports = {
  getAllNotifications,
  addNotification,
  sendNotification
};
