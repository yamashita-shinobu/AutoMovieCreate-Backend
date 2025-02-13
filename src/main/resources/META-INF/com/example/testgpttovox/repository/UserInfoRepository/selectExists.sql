select
  exists
(
  select
    *
  from
    mstknr.user_info
  where
    user_id = /* userId */''
)