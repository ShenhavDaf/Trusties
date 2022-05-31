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
        if (friend.friends[j] != currID) temp.push(friend.friends[j]);
      }
    }
    // Remove duplicates
    const unique = temp.filter(
      (value, index, self) =>
        index === self.findIndex((t) => String(t) === String(value))
    );
    // uniqueArray = a.filter(function(item, pos, self) {
    // return self.indexOf(item) == pos;
    // })

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
        if (friend.friends[j] != currID) temp.push(friend.friends[j]);
      }
    }
    for (let i = 0; i < temp.length; i++) {
      const friend = await User.findById(temp[i]);
      for (let j = 0; j < friend.friends.length; j++) {
        if (friend.friends[j] != currID) list.push(friend.friends[j]);
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

const addFriendToMyContacts = async (req, res, next) => {
  console.log("add new friend ");

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
            $push: { friends: otherUser._id },
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
            $push: { friends: me._id },
          }
        );
      }
      resolve("done");
    });
  });
  myPromise.then((alert) => {
    res.status(200).send(me);
    console.log(alert);
  });
};

const removeFriendFromMyContacts = async (req, res, next) => {
  console.log("remove this friend ");

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
    console.log(alert);
  });
};

//# PARAMS
// req.params.id -user
//req.body.stars -stars number
const rateMyHelp = async (req, res, next) => {
  console.log("## RATE MY HELP");

  var stars_raitng = parseFloat(req.body.stars) * 0.5;
  console.log("## STARS RATING NUMBER");
  console.log(stars_raitng);

  User.findById({ _id: req.params.id }).exec((err, user) => {
    if (err) {
      res.status(400).send({
        status: "fail",
        error: error.message,
        message: "user not found in DB",
      });
    }

    user.rating += stars_raitng;
    //user.rating += 1.4;

    user.save(function (err) {
      if (err) {
        console.log("ERROR!");
        console.log(err.message);

        res.status(400).send({
          status: "fail",
          error: err.message,
        });
      } else {
        console.log("USER SAVED!");
      }
    });
  });
};

//# PARAMS
// req.query.id -user
const getMyRelatedPosts = async (req, res) => {
  try {
    console.log("## Params -- " + req.params.id);

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
          if (myThirdCircle_str.includes(post.sender.valueOf())) {
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
              if (user.myFirstCircle_str.includes(senderId)) {
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
};
