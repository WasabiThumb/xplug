
--[[
  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
]]

local audiences = server.GetPlayers()
table.insert(audiences, server.GetConsole())
for _,v in pairs(audiences) do v:SendMessage("<rainbow>Hello World!</rainbow>") end