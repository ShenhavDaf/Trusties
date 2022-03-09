const User = require("../Models/user_model");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const sendEmail = require("../Utils/sendEmail");
// const verifyRandomCode = require("../Utils/");

const sendError = (res, code, msg) => {
  return res.status(code).send({
    status: "fail",
    error: msg,
  });
};

function getRandomInt(min, max) {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min) + min);
}

var randomCode = 0;
const register = async (req, res, next) => {
  const email = req.body.email;
  const password = req.body.password;
  //Hen's Addition
  const name = req.body.name;

  try {
    const exists = await User.findOne({ email: email });

    if (exists != null) return sendError(res, 400, "user exist");

    const salt = await bcrypt.genSalt(10);
    const hashPwd = await bcrypt.hash(password, salt);

    const user = User({
      email: email,
      password: hashPwd,
      name: name,
    });

    //send verify email
    randomCode = getRandomInt(1000, 9999);
    await sendEmail(user.email, "Verify Email", String(randomCode));

    newUser = await user.save();
    res.status(200).send(newUser);
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const login = async (req, res, next) => {
  const email = req.body.email;
  const password = req.body.password;

  if (email == null || password == null)
    return sendError(res, 400, "Wrong email or password");

  try {
    const user = await User.findOne({ email: email });
    if (user == null) return sendError(res, 400, "Wrong email or password");

    const match = await bcrypt.compare(password, user.password);
    if (!match) return sendError(res, 400, "Wrong email or password");

    const accessToken = await jwt.sign(
      { id: user._id },
      process.env.ACCESS_TOKEN_SECRET,
      { expiresIn: process.env.JWT_TOKEN_EXPIRATION }
    );

    res.status(200).send({ accessToken: accessToken });
  } catch (err) {
    return sendError(res, 400, err.message);
  }
};

const logout = async (req, res, next) => {
  res.status(400).send({
    status: "fail",
    error: "not implemented",
  });
};

const verifyRandomCode = async (req, res, next) => {
  var verify = randomCode;

  //TODO!!!!!- maybe to save a the generated code in mongo( with crypto)
  // and then the client side will read the data ( with GET )
};

module.exports = {
  login,
  register,
  logout,
  verifyRandomCode,
};
