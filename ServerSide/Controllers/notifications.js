const User = require("../Models/user_model");
const Post = require("../Models/post_model");
const Sos = require("../Models/sos_model");
const Comment = require("../Models/comment_model");
const Notification = require("../Models/notification_model");
const { object, getValue } = require("mongoose/lib/utils");
const request = require("request");
const { ObjectId } = require("mongodb");
var mongoose = require("mongoose");
// const admin = require("firebase-admin");
// const serviceAccount = require("../trusties-firebase-adminsdk.json");
// const { FIREBASE_CONFIG_VAR } = require("firebase-admin/lib/app/lifecycle");

let numberOfNotificationsByUser;

const getAllNotifications = async (req, res, next) => {
  Notification.find({}, function (err, docs) {
    if (err) console.log(err);
    else res.status(200).send(docs);
  });
};

const getAllNotificationsByID = async (req, res, next) => {
  try {
    Post.find({ sender: req.params.id }, function (err, postDocs) {
      if (err) console.log(err);
      else {
        Notification.find({}, async function (err, notificationDocs) {
          const resList = [];
          for (let i = 0; i < notificationDocs.length; i++) {
            if (
              notificationDocs[i]["type"] === "friendRequest" ||
              notificationDocs[i]["type"] === "approveFriendRequest"
            ) {
              const post = notificationDocs[i]["post"]; // User
              if (req.params.id === post.toString()) {
                resList.push(notificationDocs[i]);
                continue;
              }
            } else if (notificationDocs[i]["type"] === "sos") {
              const user = await User.findOne({
                _id: notificationDocs[i]["sender"],
              });
              const friendsList = user.friends;

              for (let j = 0; j < friendsList.length; j++) {
                // First circle
                if (friendsList[j]._id.toString() === req.params.id) {
                  resList.push(notificationDocs[i]);
                  // Second and third circle
                } else if (notificationDocs[i]["circle"] !== "1") {
                  for (let h = 0; h < friendsList.length; h++) {
                    const friend = await User.findOne({ _id: friendsList[h] });
                    for (let r = 0; r < friend.friends.length; r++) {
                      // Second circle
                      if (friend.friends[r]._id.toString() === req.params.id) {
                        resList.push(notificationDocs[i]);
                        continue;
                        // Third circle
                      } else if (notificationDocs[i]["circle"] === "3") {
                        const friend2 = await User.findOne({
                          _id: friend.friends[r],
                        });
                        for (let h = 0; h < friend2.friends.length; h++) {
                          if (
                            friend2.friends[h]._id.toString() === req.params.id
                          ) {
                            resList.push(notificationDocs[i]);
                            continue;
                          }
                        }
                      }
                    }
                  }
                }
              }
              continue;
            } else if (notificationDocs[i]["type"] === "approveVolunteer") {
              const volunteer = await User.findOne({
                _id: notificationDocs[i]["sender"],
              });
              const post = await Post.findOne({
                _id: notificationDocs[i]["post"],
              });
              const creator = await User.findOne({ _id: post.sender });
              if (volunteer._id.toString() === req.params.id) {
                // Switch between volunteer to sos call writer
                notificationDocs[i]["sender"] = creator._id;
                resList.push(notificationDocs[i]);
              }
            } else {
              const post = notificationDocs[i]["post"];
              for (let j = 0; j < postDocs.length; j++) {
                if (
                  postDocs[j]._id.toString() === post.toString() &&
                  notificationDocs[i]["sender"].toString() !== req.params.id
                ) {
                  resList.push(notificationDocs[i]);
                }
              }
            }
          }
          if (err) console.log(err);
          else
            numberOfNotificationsByUser = resList.filter(
              (item, index) => resList.indexOf(item) === index
            ).length;
          res
            .status(200)
            .send(
              resList.filter((item, index) => resList.indexOf(item) === index)
            );
        });
      }
    });
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const addNotification = async (req, res, next) => {
  let post;
  if (
    req.body.type === "friendRequest" ||
    req.body.type === "approveFriendRequest"
  ) {
    post = await User.findOne({ _id: req.body.post });
  } else {
    post = await Post.findOne({ _id: req.body.post });
  }

  const user = await User.findOne({ _id: req.body.sender });
  const time = req.body.time;
  const type = req.body.type;
  const circle = req.body.circle;

  const notification = Notification({
    sender: user,
    post: post,
    time: Date.now(),
    type: type,
    circle: circle,
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
// const sendNotification = async (req, res, next) => {
//   const post = await Post.findOne({ _id: req.body.post });
//   const token_db = (await User.findOne({ _id: post.sender })).firebaseToken;

//   const token = [token_db];

//   const options = {
//     priority: "high",
//     timeToLive: 60 * 60 * 24
//   };

//   const payload = {
//     notification: {
//       title: "Test Notification",
//       body: "Hey..."
//     }
//   }
//   admin.initializeApp({
//     credential: admin.credential.cert(serviceAccount)
//   });

//   admin.messaging().sendToDevice(token, payload, options)
//     .then(response => {

//       console.log("## Inside admin.messaging()");
//       console.log(response);
//       res.status(200).send("OK");

//     })
//     .catch(error => {
//       console.log(error);
//     });
//   // request.post({
//   //   headers: { 'content-type': 'application/json', "Authorization": process.env.FIREBASE_TOKEN },
//   //   url: "https://fcm.googleapis.com/fcm/send",
//   //   body: payload,
//   //   json: true,
//   // });
// };

// ## Old version

const sendNotification = async (req, res, next) => {
  console.log("Send notification");
  const post = await Post.findOne({ _id: req.body.post });
  const token = (await User.findOne({ _id: post.sender })).firebaseToken;

  const payload = {
    to: token,
    notification: {
      title: "Test Notification",
      body: "Hey...",
    },
  };

  request.post(
    {
      headers: {
        "content-type": "application/json",
        Authorization: process.env.FIREBASE_TOKEN,
      },
      url: "https://fcm.googleapis.com/fcm/send",
      body: payload,
      json: true,
    },
    (res) => {
      console.log(res);
    }
  );
};

const numberOfNewNotifications = async (req, res) => {
  const user = await User.findById(req.params.id);
  const fromUser = user.notificationsNumber;
  const fromCollection = req.query.numFromDB;
  const diff = fromCollection - fromUser;
  await res.status(200).send({ diff });
};

const updateUserNotifications = async (req, res) => {
  const exists = await User.updateOne(
    { _id: req.params.id },
    { notificationsNumber: req.query.numFromDB }
  );
  let flag = true;
  let status = 200;
  if (exists == null) {
    flag = false;
    status = 400;
  }
  await res.status(status).send({ flag });
};

module.exports = {
  getAllNotifications,
  getAllNotificationsByID,
  addNotification,
  sendNotification,
  numberOfNewNotifications,
  updateUserNotifications,
};
