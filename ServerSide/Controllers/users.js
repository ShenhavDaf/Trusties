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

module.exports = {
  // findUserByEmail,
  // findUserById,
  // editUser,
  getFriendsList,
};
