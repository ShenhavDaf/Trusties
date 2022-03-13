const User = require("../Models/user_model");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const sendEmail = require("../Utils/sendEmail");

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

var randomCode = 0;
var email;
const register = async (req, res, next) => {
  email = req.body.email;
  const password = req.body.password;
  const name = req.body.name;
  const phone = req.body.phone;

  try {
    const exists = await User.findOne({ email: email });

    if (exists != null) return sendError(res, 400, "user exist");

    const salt = await bcrypt.genSalt(10);
    const hashPwd = await bcrypt.hash(password, salt);

    const user = User({
      email: email,
      password: hashPwd,
      name: name,
      phone: phone,
    });

    randomCode = getRandomInt();
    await sendEmail(user.email, "Verify Email", String(randomCode));

    newUser = await user.save();
    res.status(200).send({ newUser, randomCode });
    // newUser = await user.save();
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

module.exports = {
  login,
  register,
  logout,
  resendEmail,
  verifiedUser,
};
