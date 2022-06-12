const { status } = require("express/lib/response");
const User = require("../Models/user_model");
const Post = require("../Models/post_model");
const psSupported = require("jsonwebtoken/lib/psSupported");

const getFriendsList = async (req, res) => {
  try {
    const circle = req.query.circle;
    const user = await User.findById(req.query.id);
    const firstList = user.friends;

    let list;

    if (circle == 1) {
      list = firstList;
    } //
    else if (circle == 2) {
      list = await findFriends(firstList, req.query.id);
    } //
    else if (circle == 3) {
      const sec = await findFriends(firstList, req.query.id);
      list = await findFriends(sec);
    }

    res.status(200).send(list);

    //
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

async function findFriends(list, currID) {
  const temp = new Array();

  for (let i = 0; i < list.length; i++) {
    if (list[i] != currID) temp.push(list[i]);

    const friend = await User.findById(list[i]);

    for (let j = 0; j < friend.friends.length; j++) {
      if (friend.friends[j] != currID) temp.push(friend.friends[j]);
    }
  }

  // Remove duplicates
  const unique = temp.filter(
    (value, index, self) =>
      index === self.findIndex((t) => String(t) === String(value))
  );

  return unique;
}

const getSecondCircleOnly = async (req, res) => {
  try {
    const user = await User.findById(req.query.id);
    const currID = req.query.id;
    const firstList = user.friends;
    const temp = new Array();

    for (let i = 0; i < firstList.length; i++) {
      const friend = await User.findById(firstList[i]);
      for (let j = 0; j < friend.friends.length; j++) {
        if (
          friend.friends[j] != currID &&
          !firstList.includes(friend.friends[j])
        )
          temp.push(friend.friends[j]);
      }
    }

    // Remove duplicates
    const unique = temp.filter(
      (value, index, self) =>
        index === self.findIndex((t) => String(t) === String(value))
    );

    res.status(200).send(unique);

    //
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const getThirdCircleOnly = async (req, res) => {
  try {
    const user = await User.findById(req.query.id);
    const currID = req.query.id;
    const firstList = user.friends;
    const temp = new Array();
    const list = new Array();
    for (let i = 0; i < firstList.length; i++) {
      const friend = await User.findById(firstList[i]);
      for (let j = 0; j < friend.friends.length; j++) {
        if (
          friend.friends[j] != currID &&
          !firstList.includes(friend.friends[j])
        ) {
          temp.push(friend.friends[j]);
        }
      }
    }

    for (let i = 0; i < temp.length; i++) {
      const friend = await User.findById(temp[i]);
      for (let j = 0; j < friend.friends.length; j++) {
        if (
          friend.friends[j] != currID &&
          !temp.includes(friend.friends[j].String)
        ) {
          list.push(friend.friends[j]);
        }
      }
    }

    // Remove duplicates
    const unique = list.filter(
      (value, index, self) =>
        index === self.findIndex((t) => String(t) === String(value))
    );

    //Check if theres a loop(third circle contains first circle)
    for (let i = 0; i < unique.length; i++) {
      if (firstList.includes(unique.at(i))) {
        unique.splice(unique.indexOf(unique.at(i)), 1);
        i--;
      }
    }

    res.status(200).send(unique);
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};


// //# PARAMS
// // req.params.myId -the user that ask for friendship
// //req.query.hisId - the one that need to approve
const addFriendToMyContacts = async (req, res, next) => {
  console.log("His: " + req.query.hisId);
  console.log("My: " + req.query.myId);

  // const his = await User.findOne({ _id: req.query.hisId });
  const me = await User.findOne({ _id: req.query.myId });


  console.log("## addFriendToMyContacts ");
  User.findById({ _id: req.query.hisId }).populate({
    path: "waitingList",
    model: "User",
  })
    .exec((err, other) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: err.message,
          message: "user not found in DB",
        });
      } else {
        other.waitingList.addToSet(me._id);
        other.save(function (err) {
          if (err) {
            res.status(400).send({
              status: "fail",
              err: err.message,
            });
          } else res.status(200).send({ status: "OK" });
        });
      }
    });
};

// //# PARAMS
// // req.query.id -user
// //req.query.hisId -the user that ask for friendship.
const approveFriend = async (req, res, next) => {

  console.log("## approveFriend");

  const me = await User.findOne({ _id: req.query.myId });
  const otherUser = await User.findOne({ _id: req.query.hisId });

  User.findById({ _id: req.query.myId }).populate({
    path: "waitingList",
    model: "User",
  }).populate({
    path: "friends",
    model: "User",
  }).exec((err, me) => {
    if (err) {
      res.status(400).send({
        status: "fail",
        error: err.message,
        message: "user not found in DB",
      });
    } else {

      for (var i = 0; i < me.waitingList.length; i++) {

        if (me.waitingList[i]._id.valueOf() == otherUser._id.valueOf()) {
          console.log("Equal");

          me.waitingList.splice(i, 1);
          me.friends.addToSet(otherUser._id);
        }
      }
      me.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
          });
        }
      });
    }
  });


  User.findById({ _id: req.query.hisId }).populate({
    path: "friends",
    model: "User",
  })
    .exec((err, other) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: err.message,
          message: "user not found in DB",
        });
      } else {


        other.friends.addToSet(me._id);
      }

      other.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
          });
        } else res.status(200).send({ status: "OK" });
      });
    });

};

// //# PARAMS
// // req.params.id -user
const getWaitingList = async (req, res) => {
  try {
    const user = await User.findById(req.query.id);
    console.log("user.waitingList");
    console.log(user.waitingList);

    res.status(200).send(user.waitingList);
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};


const removeFriendFromMyContacts = async (req, res, next) => {
  const me = await User.findOne({ _id: req.query.myId });
  const otherUser = await User.findOne({ _id: req.query.hisId });

  const myPromise = new Promise((resolve, reject) => {
    me.save(async (error) => {
      if (error) {
        res.status(400).send({
          status: "fail",
          error: error.message,
        });
      } else {
        await User.updateOne(
          { _id: me._id },
          {
            $pull: { friends: otherUser._id },
          }
        );
        resolve("done");
      }
    });

    otherUser.save(async (error) => {
      if (error) {
        res.status(400).send({
          status: "fail",
          error: error.message,
        });
      } else {
        await User.updateOne(
          { _id: otherUser._id },
          {
            $pull: { friends: me._id },
          }
        );
      }
      resolve("done");
    });
  });
  myPromise.then((alert) => {
    res.status(200).send(me);
  });
};

//# PARAMS
// req.params.id -user
//req.body.stars -stars number
const rateMyHelp = async (req, res, next) => {
  var stars_raitng = parseFloat(req.body.stars);

  User.findById({ _id: req.params.id }).exec((err, user) => {
    if (err) {
      res.status(400).send({
        status: "fail",
        error: error.message,
        message: "user not found in DB",
      });
    } else {
      user.rating = user.rating * user.numberReviews;
      user.numberReviews += 1;

      user.rating = (user.rating + stars_raitng) / user.numberReviews;

      user.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
          });
        } else res.status(200).send({ status: "OK" });
      });
    }
  });
};

//# PARAMS
// req.query.id -user
const getMyRelatedPosts = async (req, res) => {
  try {
    const user = await User.findById(req.params.id);
    var myFirstCircle = user.friends;
    var mySecondCircle = await findFriends(myFirstCircle, user.id);
    var myThirdCircle = await findFriends(mySecondCircle, user.id);

    var myFirstCircle_str = await ConvertToStringArr(myFirstCircle);
    var mySecondCircle_str = await ConvertToStringArr(mySecondCircle);
    var myThirdCircle_str = await ConvertToStringArr(myThirdCircle);

    myFirstCircle_str.push(user._id.valueOf());
    mySecondCircle_str.push(user._id.valueOf());
    myThirdCircle_str.push(user._id.valueOf());

    var myFeed = new Array();

    Post.find({}).exec(function (err, docs) {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: err.message,
        });
      } else {
        var myRelatedPosts = docs.filter((post) => {
          if (
            myThirdCircle_str != null &&
            post.sender != null &&
            myThirdCircle_str.includes(post.sender.valueOf())
          ) {
            return post;
          }
        });

        for (var i = 0; i < myRelatedPosts.length; i++) {
          var post = myRelatedPosts[i];
          var senderId = post.sender.valueOf();
          var circle = post.friends_circle;
          //SOS
          if (post.role == "SOS") {
            if (circle == 1) {
              if (myFirstCircle_str.includes(senderId)) {
                myFeed.push(post);
              }
            }
            if (circle == 2) {
              if (mySecondCircle_str.includes(senderId)) {
                myFeed.push(post);
              }
            }

            if (circle == 3) {
              if (myThirdCircle_str.includes(senderId)) {
                myFeed.push(post);
              }
            }
          }

          //QUESTION
          else {
            if (myFirstCircle_str.includes(senderId)) {
              myFeed.push(post);
            }
          }
        }
        res.status(200).send(myFeed);
      }
    });
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

//# PARAMS
// req.query.id -user
const getRating = async (req, res) => {
  try {
    const user = await User.findById(req.query.id);
    const Obj = {
      rating: user.rating,
    };
    res.status(200).send(Obj);
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

//--------------------Helpers------------------//

async function ConvertToStringArr(ObjectIdArr) {
  var StringArr = new Array();
  ObjectIdArr.forEach((el) => {
    StringArr.push(el.valueOf());
  });

  return StringArr;
}

module.exports = {
  getFriendsList,
  getSecondCircleOnly,
  getThirdCircleOnly,
  addFriendToMyContacts,
  removeFriendFromMyContacts,
  rateMyHelp,
  getRating,
  getMyRelatedPosts,
  getWaitingList,
  approveFriend,
};
