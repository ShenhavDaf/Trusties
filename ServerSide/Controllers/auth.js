const User = require("../Models/user_model");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const sendEmail = require("../Utils/sendEmail");
const { send } = require("express/lib/response");

const sendError = (res, code, msg) => {
  return res.status(code).send({
    status: "fail",
    error: msg,
  });
};

function getRandomInt() {
  min = Math.ceil(10000);
  max = Math.floor(99999);
  return Math.floor(Math.random() * (max - min) + min);
}

function getRandomPassword() {
  let password = "";

  const chars =
    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; //!@#$%^&*()

  for (let i = 0; i < 10; i++) {
    const randIndex = Math.floor(Math.random() * chars.length);
    password += chars.substring(randIndex, randIndex + 1);
  }
  return password;
}

var randomCode = 0;
var email;
var currUser;
var accessToken;

const getCurrUser = async (req, res, next) => {
  var idUser;
  try {
    if (currUser == undefined || currUser == null) {
      idUser = "null";
      accessToken = null;
    } else if (currUser.isSignedIn == false) {
      idUser = "null";
      accessToken = null;
    } else {
      idUser = currUser._id;
      console.log("INNNNN" + currUser.name);
      accessToken = await jwt.sign(
        { id: idUser },
        process.env.ACCESS_TOKEN_SECRET,
        { expiresIn: process.env.JWT_TOKEN_EXPIRATION }
      );
    }
    res.status(200).send({ accessToken, id: idUser });
  } catch (err) {
    console.log(err.message);
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const register = async (req, res, next) => {
  email = req.body.email;
  const password = req.body.password;
  const name = req.body.name;
  const phone = req.body.phone;
  var photo = req.body.photo;

  try {
    const exists = await User.findOne({ email: email });

    if (exists != null && exists.verified == true)
      return sendError(res, 400, "user exist");
    else if (exists != null && exists.verified == false) {
      if (req.body.fragment == "SignUpFragment") {
        return sendError(res, 400, "already registered");
      } else if (req.body.fragment == "LoginFragment") {
        const salt = await bcrypt.genSalt(10);
        const hashPwd = await bcrypt.hash(password, salt);

        const user = User({
          email: email,
          password: hashPwd,
          name: name,
          phone: phone,
          photo: photo,
        });

        randomCode = getRandomInt();
        await sendEmail(user.email, "Verify Email", String(randomCode));

        accessToken = await jwt.sign(
          { id: user._id },
          process.env.ACCESS_TOKEN_SECRET,
          { expiresIn: process.env.JWT_TOKEN_EXPIRATION }
        );
        user.isSignedIn = true;
        currUser = user;

        res.status(200).send({ accessToken, user, randomCode });
      }
    } else if (exists == null) {
      const salt = await bcrypt.genSalt(10);
      const hashPwd = await bcrypt.hash(password, salt);

      const user = User({
        email: email,
        password: hashPwd,
        name: name,
        phone: phone,
        photo: photo,
        isSignedIn: true,
      });

      randomCode = getRandomInt();
      await sendEmail(user.email, "Verify Email", String(randomCode));
      newUser = await user.save();
      user.isSignedIn = true;
      currUser = user;
      console.log("currentInRegister" + currUser.name);
      console.log("currentInRegister" + currUser.isSignedIn);

      accessToken = await jwt.sign(
        { id: user._id },
        process.env.ACCESS_TOKEN_SECRET,
        { expiresIn: process.env.JWT_TOKEN_EXPIRATION }
      );

      res.status(200).send({ accessToken, newUser, randomCode });
    }
  } catch (err) {
    console.log(err.message);
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const login = async (req, res, next) => {
  const email = req.body.email;
  const password = req.body.password;
  const token = req.body.firebaseToken;

  if (email == null || password == null)
    return sendError(res, 400, "Wrong email or password");

  try {
    const user = await User.findOne({ email: email });

    if (user == null) return sendError(res, 400, "Please register first");

    const match = await bcrypt.compare(password, user.password);
    if (!match) return sendError(res, 400, "Incorrect password");

    accessToken = await jwt.sign(
      { id: user._id },
      process.env.ACCESS_TOKEN_SECRET,
      { expiresIn: process.env.JWT_TOKEN_EXPIRATION }
    );
    await User.updateOne(
      { email: email },
      {
        $set: {
          firebaseToken: token,
          isSignedIn: true,
        },
      }
    );
    user.isSignedIn = true;
    currUser = user;
    console.log("currentInLogin" + currUser.name);
    console.log("currentInLogin" + currUser.isSignedIn);
    // const refreshToken = await jwt.sign(
    //   { id: user._id },
    //   process.env.REFRESH_TOKEN_SECRET
    // );

    // if (user.tokens == null) user.tokens = [refreshToken];
    // else user.tokens.push(refreshToken);
    // await user.save();

    // console.log(user.tokens);

    res.status(200).send({
      accessToken: accessToken,
      // refreshToken: refreshToken,
      user,
    });
  } catch (err) {
    return sendError(res, 400, err.message);
  }
};

const logout = async (req, res, next) => {
  console.log("inside logout");
  try {
    const exists = await User.updateOne(
      { _id: req.query.id },
      {
        isSignedIn: false,
      }
    );
    if (exists == null) return sendError(res, 400, "user does not exist");
    currUser = null;
    res.status(200).send({
      _id: req.query.id,
    });
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }

  // const authHeader = req.headers["authorization"];
  // const token = authHeader && authHeader.split(" ")[1];
  // if (token == null) return res.sendStatus(401);

  // jwt.verify(token, process.env.REFRESH_TOKEN_SECRET, async (err, userInfo) => {
  //   if (err) return res.status(403).send(err.message);
  //   const userID = userInfo._id;
  //   try {
  //     const user = await User.findById(userID);

  //     if (user == null) return res.status(403).send("invalid request");

  //     if (!user.tokens.includes(token)) {
  //       user.tokens = [];
  //       await user.save();
  //       return res.status(403).send("invalid request");
  //     }

  //     user.tokens.splice(user.tokens.indexOf(token), 1);
  //     await user.save();
  //     res.status(200).send();
  //   } catch (error) {
  //     res.status(403).send(error.message);
  //   }
  // });
};

const refreshToken = async (req, res) => {
  console.log("refreshToken");

  res.status(400).send({
    status: "fail",
    message: "not implemented",
  });

  //   const authHeader = req.headers["authorization"];
  //   const token = authHeader && authHeader.split(" ")[1];
  //   if (token == null) return res.sendStatus(401);

  //   jwt.verify(token, process.env.REFRESH_TOKEN_SECRET, async (err, userInfo) => {
  //     if (err) return res.status(403).send(err.message);
  //     const userID = userInfo._id;
  //     try {
  //       const user = await User.findById(userID);

  //       if (user == null) return res.status(403).send("invalid request");

  //       if (!user.tokens.includes(token)) {
  //         user.tokens = [];
  //         await user.save();
  //         return res.status(403).send("invalid request");
  //       }

  //       const accessToken = await jwt.sign(
  //         { id: user._id },
  //         process.env.ACCESS_TOKEN_SECRET,
  //         { expiresIn: process.env.JWT_TOKEN_EXPIRATION }
  //       );

  //       const refreshToken = await jwt.sign(
  //         { id: user._id },
  //         process.env.REFRESH_TOKEN_SECRET
  //       );

  //       user.tokens[user.tokens.indexOf(token)] = refreshToken;
  //       await user.save();
  //       res.status(200).send({
  //         accessToken: accessToken,
  //         refreshToken: refreshToken,
  //       });
  //     } catch (error) {
  //       res.status(403).send(error.message);
  //     }
  //   });
};
/* ----------------------------------------------------------- */
/* ----------------------------------------------------------- */
/* ----------------------------------------------------------- */

const resendEmail = async (req, res, next) => {
  randomCode = getRandomInt();
  await sendEmail(email, "Verify Email", String(randomCode));

  res.status(200).send(String(randomCode));
};

const verifiedUser = async (req, res, next) => {
  try {
    const exists = await User.updateOne(
      { email: email },
      {
        verified: true,
      }
    );
    if (exists == null) return sendError(res, 400, "user does not exist");
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const getAllUsers = async (req, res, next) => {
  User.find({}, function (err, docs) {
    if (err) console.log(err);
    else res.status(200).send(docs);
  });
};

//Find user by email
const findUserByEmail = async (req, res, next) => {
  try {
    const user = await User.findOne({ email: req.query.emailAddress });
    if (user == null) return sendError(res, 400, "user does not exist");
    res.status(200).send({
      _id: user._id,
      name: user.name,
      email: user.email,
      phone: user.phone,
      raiting: user.raiting,
    });
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

//Find user by id
const findUserById = async (req, res, next) => {
  try {
    const user = await User.findOne({ _id: req.query.id });
    // console.log(user);
    if (user == null) return sendError(res, 400, "user does not exist");
    res.status(200).send({
      name: user.name,
      email: user.email,
      phone: user.phone,
      raiting: user.raiting,
      photo: user.photo,
      friends: user.friends,
    });
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

//GET function
const forgotPassword = async (req, res) => {
  try {
    console.log(
      "--------- user email from adnroid --> " + req.query.emailAddress
    );
    const currUser = await User.findOne({ email: req.query.emailAddress });
    if (currUser) {
      const randomPass = getRandomPassword();
      console.log("--------- new password --> " + randomPass);

      const salt = await bcrypt.genSalt(10);
      const hashPwd = await bcrypt.hash(randomPass, salt);

      currUser.password = hashPwd;
      newUser = await currUser.save();

      sendEmail(req.query.emailAddress, "Password reset", randomPass);
    }
    // currUser == null
    else {
      // TODO
      console.log("-------- not a regiser user");
    }
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: error.message,
    });
  }
};

const editUser = async (req, res, next) => {
  const flagPassword = req.body.flag;
  try {
    const exists = await User.updateOne(
      { _id: req.params.id },
      {
        name: req.body.name,
        phone: req.body.phone,
        photo: req.body.photo,
      }
    );
    const updateUser = await User.findById(req.params.id);

    if (flagPassword == "1") {
      console.log("inside flaG PASSWORD");
      const match = await bcrypt.compare(
        req.body.currPassword,
        updateUser.password
      );
      if (!match) {
        console.log("~~~~~~~~Incorrect password~~~~~~~~~");
        res.status(400).send({
          status: "cuur password incorrect",
          error: err.message,
        });
      }
      const salt = await bcrypt.genSalt(10);
      const hashPwd = await bcrypt.hash(req.body.newPassword, salt);
      const chnagePass = await User.updateOne(
        { _id: req.params.id },
        {
          password: hashPwd,
        }
      );
    }

    if (exists == null) return sendError(res, 400, "post does not exist");
    else {
      console.log("user edited!");
      res.status(200).send({
        status: "OK",
        user: updateUser,
      });
    }
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

module.exports = {
  login,
  register,
  logout,
  refreshToken,
  getCurrUser,

  resendEmail,
  verifiedUser,
  forgotPassword,
  findUserByEmail,
  findUserById,

  editUser,
  getAllUsers,
};
