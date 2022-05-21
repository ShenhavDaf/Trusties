const { status } = require("express/lib/response");
const User = require("../Models/user_model");

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

  const stars_raitng = req.body.stars;
  console.log("## STARS RATING NUMBER");
  console.log(stars_raitng);


  User.findById(req.params.id)
    .exec((err, user) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: "user not found in DB",
        });
      }

      var number = user.rating + (stars_raitng * 0.5);
      if (number > 5) {
        user.rating = 5;
      }
      else {
        user.rating = number;
      }
      user.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
            message: "user Rating NOT saved",
          });
        }
        else {
          console.log("## SAVED USER");
          console.log(user);

        }
      });
    });
};

module.exports = {
  // findUserByEmail,
  // findUserById,
  // editUser,
  getFriendsList,
  getSecondCircleOnly,
  getThirdCircleOnly,
  addFriendToMyContacts,
  removeFriendFromMyContacts,
  rateMyHelp,
};
